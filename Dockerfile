FROM elixir:1.13-alpine as builder

ENV MIX_ENV=prod

WORKDIR /app

RUN apk add --no-cache --update git build-base ca-certificates zstd

RUN mkdir config
COPY config/ ./config
COPY . .
COPY mix.exs .
COPY mix.lock .

RUN mix local.rebar --force \
    && mix local.hex --force \
    && mix deps.get \
    && mix deps.get \
    && mix release.init

RUN echo "-name spawn@${HOSTNAME}" >> ./rel/vm.args.eex \
    && echo "-setcookie ${NODE_COOKIE}" >> ./rel/vm.args.eex

RUN MIX_ENV=prod mix release

# ---- Application Stage ----
FROM alpine:3
RUN apk add --no-cache --update bash openssl build-base ca-certificates zstd

WORKDIR /home/app
COPY --from=builder /app/_build .

RUN adduser app --disabled-password --home app
RUN chown -R app: ./prod
USER app

ENV MIX_ENV=prod

ENTRYPOINT ["./prod/rel/proxy/bin/proxy", "start"]