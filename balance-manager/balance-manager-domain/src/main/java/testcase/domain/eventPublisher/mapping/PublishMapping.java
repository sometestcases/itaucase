package testcase.domain.eventPublisher.mapping;


import org.apache.avro.generic.GenericRecord;

public interface PublishMapping<T extends GenericRecord> {

    public Class<T> getMappedClass();

    public String getTopic();

    public String getKey(T genericRecord);
}
