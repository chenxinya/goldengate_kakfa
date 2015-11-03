/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.rogers.goldengate.avro;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class InsertMutation extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"InsertMutation\",\"namespace\":\"com.rogers.goldengate.avro\",\"fields\":[{\"name\":\"schema\",\"type\":\"string\"},{\"name\":\"table\",\"type\":\"string\"},{\"name\":\"strings\",\"type\":{\"type\":\"map\",\"values\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"avro.java.string\":\"String\"},\"avro.java.string\":\"String\"}],\"avro.java.string\":\"String\"}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence schema;
  @Deprecated public java.lang.CharSequence table;
  @Deprecated public java.util.Map<java.lang.CharSequence,java.lang.CharSequence> strings;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public InsertMutation() {}

  /**
   * All-args constructor.
   */
  public InsertMutation(java.lang.CharSequence schema, java.lang.CharSequence table, java.util.Map<java.lang.CharSequence,java.lang.CharSequence> strings) {
    this.schema = schema;
    this.table = table;
    this.strings = strings;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return schema;
    case 1: return table;
    case 2: return strings;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: schema = (java.lang.CharSequence)value$; break;
    case 1: table = (java.lang.CharSequence)value$; break;
    case 2: strings = (java.util.Map<java.lang.CharSequence,java.lang.CharSequence>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'schema' field.
   */
  public java.lang.CharSequence getSchema$() {
    return schema;
  }

  /**
   * Sets the value of the 'schema' field.
   * @param value the value to set.
   */
  public void setSchema$(java.lang.CharSequence value) {
    this.schema = value;
  }

  /**
   * Gets the value of the 'table' field.
   */
  public java.lang.CharSequence getTable() {
    return table;
  }

  /**
   * Sets the value of the 'table' field.
   * @param value the value to set.
   */
  public void setTable(java.lang.CharSequence value) {
    this.table = value;
  }

  /**
   * Gets the value of the 'strings' field.
   */
  public java.util.Map<java.lang.CharSequence,java.lang.CharSequence> getStrings() {
    return strings;
  }

  /**
   * Sets the value of the 'strings' field.
   * @param value the value to set.
   */
  public void setStrings(java.util.Map<java.lang.CharSequence,java.lang.CharSequence> value) {
    this.strings = value;
  }

  /** Creates a new InsertMutation RecordBuilder */
  public static com.rogers.goldengate.avro.InsertMutation.Builder newBuilder() {
    return new com.rogers.goldengate.avro.InsertMutation.Builder();
  }
  
  /** Creates a new InsertMutation RecordBuilder by copying an existing Builder */
  public static com.rogers.goldengate.avro.InsertMutation.Builder newBuilder(com.rogers.goldengate.avro.InsertMutation.Builder other) {
    return new com.rogers.goldengate.avro.InsertMutation.Builder(other);
  }
  
  /** Creates a new InsertMutation RecordBuilder by copying an existing InsertMutation instance */
  public static com.rogers.goldengate.avro.InsertMutation.Builder newBuilder(com.rogers.goldengate.avro.InsertMutation other) {
    return new com.rogers.goldengate.avro.InsertMutation.Builder(other);
  }
  
  /**
   * RecordBuilder for InsertMutation instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<InsertMutation>
    implements org.apache.avro.data.RecordBuilder<InsertMutation> {

    private java.lang.CharSequence schema;
    private java.lang.CharSequence table;
    private java.util.Map<java.lang.CharSequence,java.lang.CharSequence> strings;

    /** Creates a new Builder */
    private Builder() {
      super(com.rogers.goldengate.avro.InsertMutation.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(com.rogers.goldengate.avro.InsertMutation.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.schema)) {
        this.schema = data().deepCopy(fields()[0].schema(), other.schema);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.table)) {
        this.table = data().deepCopy(fields()[1].schema(), other.table);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.strings)) {
        this.strings = data().deepCopy(fields()[2].schema(), other.strings);
        fieldSetFlags()[2] = true;
      }
    }
    
    /** Creates a Builder by copying an existing InsertMutation instance */
    private Builder(com.rogers.goldengate.avro.InsertMutation other) {
            super(com.rogers.goldengate.avro.InsertMutation.SCHEMA$);
      if (isValidValue(fields()[0], other.schema)) {
        this.schema = data().deepCopy(fields()[0].schema(), other.schema);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.table)) {
        this.table = data().deepCopy(fields()[1].schema(), other.table);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.strings)) {
        this.strings = data().deepCopy(fields()[2].schema(), other.strings);
        fieldSetFlags()[2] = true;
      }
    }

    /** Gets the value of the 'schema' field */
    public java.lang.CharSequence getSchema$() {
      return schema;
    }
    
    /** Sets the value of the 'schema' field */
    public com.rogers.goldengate.avro.InsertMutation.Builder setSchema$(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.schema = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'schema' field has been set */
    public boolean hasSchema$() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'schema' field */
    public com.rogers.goldengate.avro.InsertMutation.Builder clearSchema$() {
      schema = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'table' field */
    public java.lang.CharSequence getTable() {
      return table;
    }
    
    /** Sets the value of the 'table' field */
    public com.rogers.goldengate.avro.InsertMutation.Builder setTable(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.table = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'table' field has been set */
    public boolean hasTable() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'table' field */
    public com.rogers.goldengate.avro.InsertMutation.Builder clearTable() {
      table = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'strings' field */
    public java.util.Map<java.lang.CharSequence,java.lang.CharSequence> getStrings() {
      return strings;
    }
    
    /** Sets the value of the 'strings' field */
    public com.rogers.goldengate.avro.InsertMutation.Builder setStrings(java.util.Map<java.lang.CharSequence,java.lang.CharSequence> value) {
      validate(fields()[2], value);
      this.strings = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'strings' field has been set */
    public boolean hasStrings() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'strings' field */
    public com.rogers.goldengate.avro.InsertMutation.Builder clearStrings() {
      strings = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    public InsertMutation build() {
      try {
        InsertMutation record = new InsertMutation();
        record.schema = fieldSetFlags()[0] ? this.schema : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.table = fieldSetFlags()[1] ? this.table : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.strings = fieldSetFlags()[2] ? this.strings : (java.util.Map<java.lang.CharSequence,java.lang.CharSequence>) defaultValue(fields()[2]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}