// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: GenericProto.proto

package com.wonderzh.cooser.protocol;

public final class GenericProto {
  private GenericProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface JsonObjOrBuilder extends
      // @@protoc_insertion_point(interface_extends:generic.JsonObj)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string body = 1;</code>
     * @return The body.
     */
    String getBody();
    /**
     * <code>string body = 1;</code>
     * @return The bytes for body.
     */
    com.google.protobuf.ByteString
        getBodyBytes();
  }
  /**
   * Protobuf type {@code generic.JsonObj}
   */
  public static final class JsonObj extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:generic.JsonObj)
      JsonObjOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use JsonObj.newBuilder() to construct.
    private JsonObj(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private JsonObj() {
      body_ = "";
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new JsonObj();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private JsonObj(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              String s = input.readStringRequireUtf8();

              body_ = s;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.wonderzh.cooser.protocol.GenericProto.internal_static_generic_JsonObj_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.wonderzh.cooser.protocol.GenericProto.internal_static_generic_JsonObj_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.wonderzh.cooser.protocol.GenericProto.JsonObj.class, com.wonderzh.cooser.protocol.GenericProto.JsonObj.Builder.class);
    }

    public static final int BODY_FIELD_NUMBER = 1;
    private volatile Object body_;
    /**
     * <code>string body = 1;</code>
     * @return The body.
     */
    @Override
    public String getBody() {
      Object ref = body_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        body_ = s;
        return s;
      }
    }
    /**
     * <code>string body = 1;</code>
     * @return The bytes for body.
     */
    @Override
    public com.google.protobuf.ByteString
        getBodyBytes() {
      Object ref = body_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        body_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getBodyBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, body_);
      }
      unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getBodyBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, body_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.wonderzh.cooser.protocol.GenericProto.JsonObj)) {
        return super.equals(obj);
      }
      com.wonderzh.cooser.protocol.GenericProto.JsonObj other = (com.wonderzh.cooser.protocol.GenericProto.JsonObj) obj;

      if (!getBody()
          .equals(other.getBody())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + BODY_FIELD_NUMBER;
      hash = (53 * hash) + getBody().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.wonderzh.cooser.protocol.GenericProto.JsonObj prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code generic.JsonObj}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:generic.JsonObj)
        com.wonderzh.cooser.protocol.GenericProto.JsonObjOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.wonderzh.cooser.protocol.GenericProto.internal_static_generic_JsonObj_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.wonderzh.cooser.protocol.GenericProto.internal_static_generic_JsonObj_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.wonderzh.cooser.protocol.GenericProto.JsonObj.class, com.wonderzh.cooser.protocol.GenericProto.JsonObj.Builder.class);
      }

      // Construct using com.wonderzh.cooser.protocol.GenericProto.JsonObj.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @Override
      public Builder clear() {
        super.clear();
        body_ = "";

        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.wonderzh.cooser.protocol.GenericProto.internal_static_generic_JsonObj_descriptor;
      }

      @Override
      public com.wonderzh.cooser.protocol.GenericProto.JsonObj getDefaultInstanceForType() {
        return com.wonderzh.cooser.protocol.GenericProto.JsonObj.getDefaultInstance();
      }

      @Override
      public com.wonderzh.cooser.protocol.GenericProto.JsonObj build() {
        com.wonderzh.cooser.protocol.GenericProto.JsonObj result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public com.wonderzh.cooser.protocol.GenericProto.JsonObj buildPartial() {
        com.wonderzh.cooser.protocol.GenericProto.JsonObj result = new com.wonderzh.cooser.protocol.GenericProto.JsonObj(this);
        result.body_ = body_;
        onBuilt();
        return result;
      }

      @Override
      public Builder clone() {
        return super.clone();
      }
      @Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.setField(field, value);
      }
      @Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.addRepeatedField(field, value);
      }
      @Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.wonderzh.cooser.protocol.GenericProto.JsonObj) {
          return mergeFrom((com.wonderzh.cooser.protocol.GenericProto.JsonObj)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.wonderzh.cooser.protocol.GenericProto.JsonObj other) {
        if (other == com.wonderzh.cooser.protocol.GenericProto.JsonObj.getDefaultInstance()) return this;
        if (!other.getBody().isEmpty()) {
          body_ = other.body_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @Override
      public final boolean isInitialized() {
        return true;
      }

      @Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.wonderzh.cooser.protocol.GenericProto.JsonObj parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.wonderzh.cooser.protocol.GenericProto.JsonObj) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private Object body_ = "";
      /**
       * <code>string body = 1;</code>
       * @return The body.
       */
      public String getBody() {
        Object ref = body_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          body_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>string body = 1;</code>
       * @return The bytes for body.
       */
      public com.google.protobuf.ByteString
          getBodyBytes() {
        Object ref = body_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          body_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string body = 1;</code>
       * @param value The body to set.
       * @return This builder for chaining.
       */
      public Builder setBody(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }

        body_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string body = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearBody() {

        body_ = getDefaultInstance().getBody();
        onChanged();
        return this;
      }
      /**
       * <code>string body = 1;</code>
       * @param value The bytes for body to set.
       * @return This builder for chaining.
       */
      public Builder setBodyBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

        body_ = value;
        onChanged();
        return this;
      }
      @Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:generic.JsonObj)
    }

    // @@protoc_insertion_point(class_scope:generic.JsonObj)
    private static final com.wonderzh.cooser.protocol.GenericProto.JsonObj DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.wonderzh.cooser.protocol.GenericProto.JsonObj();
    }

    public static com.wonderzh.cooser.protocol.GenericProto.JsonObj getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<JsonObj>
        PARSER = new com.google.protobuf.AbstractParser<JsonObj>() {
      @Override
      public JsonObj parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new JsonObj(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<JsonObj> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<JsonObj> getParserForType() {
      return PARSER;
    }

    @Override
    public com.wonderzh.cooser.protocol.GenericProto.JsonObj getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_generic_JsonObj_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_generic_JsonObj_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\022GenericProto.proto\022\007generic\"\027\n\007JsonObj" +
      "\022\014\n\004body\030\001 \001(\tB2\n\"com.wonderzh.cooser.coo" +
      "ser.protocolB\014GenericProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_generic_JsonObj_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_generic_JsonObj_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_generic_JsonObj_descriptor,
        new String[] { "Body", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
