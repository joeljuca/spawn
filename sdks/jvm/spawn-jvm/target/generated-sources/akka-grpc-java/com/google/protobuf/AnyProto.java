// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/protobuf/any.proto

package com.google.protobuf;

public final class AnyProto {
  private AnyProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_protobuf_Any_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_protobuf_Any_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\031google/protobuf/any.proto\022\017google.prot" +
      "obuf\"&\n\003Any\022\020\n\010type_url\030\001 \001(\t\022\r\n\005value\030\002" +
      " \001(\014Bv\n\023com.google.protobufB\010AnyProtoP\001Z" +
      ",google.golang.org/protobuf/types/known/" +
      "anypb\242\002\003GPB\252\002\036Google.Protobuf.WellKnownT" +
      "ypesb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_google_protobuf_Any_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_google_protobuf_Any_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_protobuf_Any_descriptor,
        new java.lang.String[] { "TypeUrl", "Value", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}