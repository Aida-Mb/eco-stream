package com.ecostream.ingestion.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(EnergyReading.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class EnergyReading_ {

	
	/**
	 * @see com.ecostream.ingestion.model.EnergyReading#meterId
	 **/
	public static volatile SingularAttribute<EnergyReading, String> meterId;
	
	/**
	 * @see com.ecostream.ingestion.model.EnergyReading#consumption
	 **/
	public static volatile SingularAttribute<EnergyReading, Double> consumption;
	
	/**
	 * @see com.ecostream.ingestion.model.EnergyReading#id
	 **/
	public static volatile SingularAttribute<EnergyReading, Long> id;
	
	/**
	 * @see com.ecostream.ingestion.model.EnergyReading#receivedAt
	 **/
	public static volatile SingularAttribute<EnergyReading, Instant> receivedAt;
	
	/**
	 * @see com.ecostream.ingestion.model.EnergyReading
	 **/
	public static volatile EntityType<EnergyReading> class_;
	
	/**
	 * @see com.ecostream.ingestion.model.EnergyReading#timestamp
	 **/
	public static volatile SingularAttribute<EnergyReading, String> timestamp;

	public static final String METER_ID = "meterId";
	public static final String CONSUMPTION = "consumption";
	public static final String ID = "id";
	public static final String RECEIVED_AT = "receivedAt";
	public static final String TIMESTAMP = "timestamp";

}

