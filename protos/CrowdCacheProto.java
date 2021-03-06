// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: crowdcacherpcProto.proto

public final class CrowdCacheProto {
  private CrowdCacheProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface CrowdCacheMsgOrBuilder extends
      // @@protoc_insertion_point(interface_extends:CrowdCacheMsg)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required .CrowdCacheMsg.MsgType msgType = 1;</code>
     */
    boolean hasMsgType();
    /**
     * <code>required .CrowdCacheMsg.MsgType msgType = 1;</code>
     */
    CrowdCacheProto.CrowdCacheMsg.MsgType getMsgType();

    /**
     * <code>required uint32 id = 2;</code>
     */
    boolean hasId();
    /**
     * <code>required uint32 id = 2;</code>
     */
    int getId();

    /**
     * <code>required bytes val = 3;</code>
     */
    boolean hasVal();
    /**
     * <code>required bytes val = 3;</code>
     */
    com.google.protobuf.ByteString getVal();
  }
  /**
   * Protobuf type {@code CrowdCacheMsg}
   */
  public  static final class CrowdCacheMsg extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:CrowdCacheMsg)
      CrowdCacheMsgOrBuilder {
    // Use CrowdCacheMsg.newBuilder() to construct.
    private CrowdCacheMsg(com.google.protobuf.GeneratedMessage.Builder builder) {
      super(builder);
    }
    private CrowdCacheMsg() {
      msgType_ = 0;
      id_ = 0;
      val_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private CrowdCacheMsg(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry) {
      this();
      int mutable_bitField0_ = 0;
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
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              int rawValue = input.readEnum();
              CrowdCacheProto.CrowdCacheMsg.MsgType value = CrowdCacheProto.CrowdCacheMsg.MsgType.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(1, rawValue);
              } else {
                bitField0_ |= 0x00000001;
                msgType_ = rawValue;
              }
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              id_ = input.readUInt32();
              break;
            }
            case 26: {
              bitField0_ |= 0x00000004;
              val_ = input.readBytes();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw new RuntimeException(e.setUnfinishedMessage(this));
      } catch (java.io.IOException e) {
        throw new RuntimeException(
            new com.google.protobuf.InvalidProtocolBufferException(
                e.getMessage()).setUnfinishedMessage(this));
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return CrowdCacheProto.internal_static_CrowdCacheMsg_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return CrowdCacheProto.internal_static_CrowdCacheMsg_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              CrowdCacheProto.CrowdCacheMsg.class, CrowdCacheProto.CrowdCacheMsg.Builder.class);
    }

    /**
     * Protobuf enum {@code CrowdCacheMsg.MsgType}
     */
    public enum MsgType
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>GET_REQ = 0;</code>
       */
      GET_REQ(0, 0),
      /**
       * <code>GET_RESP = 1;</code>
       */
      GET_RESP(1, 1),
      ;

      /**
       * <code>GET_REQ = 0;</code>
       */
      public static final int GET_REQ_VALUE = 0;
      /**
       * <code>GET_RESP = 1;</code>
       */
      public static final int GET_RESP_VALUE = 1;


      public final int getNumber() {
        return value;
      }

      public static MsgType valueOf(int value) {
        switch (value) {
          case 0: return GET_REQ;
          case 1: return GET_RESP;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<MsgType>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static com.google.protobuf.Internal.EnumLiteMap<MsgType>
          internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<MsgType>() {
              public MsgType findValueByNumber(int number) {
                return MsgType.valueOf(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(index);
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return CrowdCacheProto.CrowdCacheMsg.getDescriptor().getEnumTypes().get(0);
      }

      private static final MsgType[] VALUES = values();

      public static MsgType valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int index;
      private final int value;

      private MsgType(int index, int value) {
        this.index = index;
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:CrowdCacheMsg.MsgType)
    }

    private int bitField0_;
    public static final int MSGTYPE_FIELD_NUMBER = 1;
    private int msgType_;
    /**
     * <code>required .CrowdCacheMsg.MsgType msgType = 1;</code>
     */
    public boolean hasMsgType() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required .CrowdCacheMsg.MsgType msgType = 1;</code>
     */
    public CrowdCacheProto.CrowdCacheMsg.MsgType getMsgType() {
      CrowdCacheProto.CrowdCacheMsg.MsgType result = CrowdCacheProto.CrowdCacheMsg.MsgType.valueOf(msgType_);
      return result == null ? CrowdCacheProto.CrowdCacheMsg.MsgType.GET_REQ : result;
    }

    public static final int ID_FIELD_NUMBER = 2;
    private int id_;
    /**
     * <code>required uint32 id = 2;</code>
     */
    public boolean hasId() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required uint32 id = 2;</code>
     */
    public int getId() {
      return id_;
    }

    public static final int VAL_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString val_;
    /**
     * <code>required bytes val = 3;</code>
     */
    public boolean hasVal() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required bytes val = 3;</code>
     */
    public com.google.protobuf.ByteString getVal() {
      return val_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasMsgType()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasVal()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeEnum(1, msgType_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeUInt32(2, id_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, val_);
      }
      unknownFields.writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, msgType_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(2, id_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, val_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    public static CrowdCacheProto.CrowdCacheMsg parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static CrowdCacheProto.CrowdCacheMsg parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static CrowdCacheProto.CrowdCacheMsg parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static CrowdCacheProto.CrowdCacheMsg parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static CrowdCacheProto.CrowdCacheMsg parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static CrowdCacheProto.CrowdCacheMsg parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static CrowdCacheProto.CrowdCacheMsg parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static CrowdCacheProto.CrowdCacheMsg parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static CrowdCacheProto.CrowdCacheMsg parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static CrowdCacheProto.CrowdCacheMsg parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(CrowdCacheProto.CrowdCacheMsg prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code CrowdCacheMsg}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:CrowdCacheMsg)
        CrowdCacheProto.CrowdCacheMsgOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return CrowdCacheProto.internal_static_CrowdCacheMsg_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return CrowdCacheProto.internal_static_CrowdCacheMsg_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                CrowdCacheProto.CrowdCacheMsg.class, CrowdCacheProto.CrowdCacheMsg.Builder.class);
      }

      // Construct using CrowdCacheProto.CrowdCacheMsg.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        msgType_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        id_ = 0;
        bitField0_ = (bitField0_ & ~0x00000002);
        val_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return CrowdCacheProto.internal_static_CrowdCacheMsg_descriptor;
      }

      public CrowdCacheProto.CrowdCacheMsg getDefaultInstanceForType() {
        return CrowdCacheProto.CrowdCacheMsg.getDefaultInstance();
      }

      public CrowdCacheProto.CrowdCacheMsg build() {
        CrowdCacheProto.CrowdCacheMsg result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public CrowdCacheProto.CrowdCacheMsg buildPartial() {
        CrowdCacheProto.CrowdCacheMsg result = new CrowdCacheProto.CrowdCacheMsg(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.msgType_ = msgType_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.val_ = val_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof CrowdCacheProto.CrowdCacheMsg) {
          return mergeFrom((CrowdCacheProto.CrowdCacheMsg)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(CrowdCacheProto.CrowdCacheMsg other) {
        if (other == CrowdCacheProto.CrowdCacheMsg.getDefaultInstance()) return this;
        if (other.hasMsgType()) {
          setMsgType(other.getMsgType());
        }
        if (other.hasId()) {
          setId(other.getId());
        }
        if (other.hasVal()) {
          setVal(other.getVal());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        if (!hasMsgType()) {
          return false;
        }
        if (!hasId()) {
          return false;
        }
        if (!hasVal()) {
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        CrowdCacheProto.CrowdCacheMsg parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (CrowdCacheProto.CrowdCacheMsg) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int msgType_ = 0;
      /**
       * <code>required .CrowdCacheMsg.MsgType msgType = 1;</code>
       */
      public boolean hasMsgType() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required .CrowdCacheMsg.MsgType msgType = 1;</code>
       */
      public CrowdCacheProto.CrowdCacheMsg.MsgType getMsgType() {
        CrowdCacheProto.CrowdCacheMsg.MsgType result = CrowdCacheProto.CrowdCacheMsg.MsgType.valueOf(msgType_);
        return result == null ? CrowdCacheProto.CrowdCacheMsg.MsgType.GET_REQ : result;
      }
      /**
       * <code>required .CrowdCacheMsg.MsgType msgType = 1;</code>
       */
      public Builder setMsgType(CrowdCacheProto.CrowdCacheMsg.MsgType value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000001;
        msgType_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <code>required .CrowdCacheMsg.MsgType msgType = 1;</code>
       */
      public Builder clearMsgType() {
        bitField0_ = (bitField0_ & ~0x00000001);
        msgType_ = 0;
        onChanged();
        return this;
      }

      private int id_ ;
      /**
       * <code>required uint32 id = 2;</code>
       */
      public boolean hasId() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required uint32 id = 2;</code>
       */
      public int getId() {
        return id_;
      }
      /**
       * <code>required uint32 id = 2;</code>
       */
      public Builder setId(int value) {
        bitField0_ |= 0x00000002;
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required uint32 id = 2;</code>
       */
      public Builder clearId() {
        bitField0_ = (bitField0_ & ~0x00000002);
        id_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString val_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>required bytes val = 3;</code>
       */
      public boolean hasVal() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required bytes val = 3;</code>
       */
      public com.google.protobuf.ByteString getVal() {
        return val_;
      }
      /**
       * <code>required bytes val = 3;</code>
       */
      public Builder setVal(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        val_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required bytes val = 3;</code>
       */
      public Builder clearVal() {
        bitField0_ = (bitField0_ & ~0x00000004);
        val_ = getDefaultInstance().getVal();
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:CrowdCacheMsg)
    }

    // @@protoc_insertion_point(class_scope:CrowdCacheMsg)
    private static final CrowdCacheProto.CrowdCacheMsg DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new CrowdCacheProto.CrowdCacheMsg();
    }

    public static CrowdCacheProto.CrowdCacheMsg getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    public static final com.google.protobuf.Parser<CrowdCacheMsg> PARSER =
        new com.google.protobuf.AbstractParser<CrowdCacheMsg>() {
      public CrowdCacheMsg parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        try {
          return new CrowdCacheMsg(input, extensionRegistry);
        } catch (RuntimeException e) {
          if (e.getCause() instanceof
              com.google.protobuf.InvalidProtocolBufferException) {
            throw (com.google.protobuf.InvalidProtocolBufferException)
                e.getCause();
          }
          throw e;
        }
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<CrowdCacheMsg> getParserForType() {
      return PARSER;
    }

    public CrowdCacheProto.CrowdCacheMsg getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_CrowdCacheMsg_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_CrowdCacheMsg_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\030crowdcacherpcProto.proto\"w\n\rCrowdCache" +
      "Msg\022\'\n\007msgType\030\001 \002(\0162\026.CrowdCacheMsg.Msg" +
      "Type\022\n\n\002id\030\002 \002(\r\022\013\n\003val\030\003 \002(\014\"$\n\007MsgType" +
      "\022\013\n\007GET_REQ\020\000\022\014\n\010GET_RESP\020\001B\021B\017CrowdCach" +
      "eProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_CrowdCacheMsg_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_CrowdCacheMsg_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_CrowdCacheMsg_descriptor,
        new java.lang.String[] { "MsgType", "Id", "Val", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
