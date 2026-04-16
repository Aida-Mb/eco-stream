package com.ecostream.billing.repository;

import com.ecostream.billing.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    public List<Bill> findByMeterIdOrderByTimestampDesc(String meterId);

    @Query("SELECT SUM(b.consumption) FROM Bill b WHERE b.meterId = :meterId")
    public Double sumConsumptionByMeterId(@Param("meterId") String meterId);

    @Query("SELECT SUM(b.cost) FROM Bill b WHERE b.meterId = :meterId")
    public Double sumCostByMeterId(@Param("meterId") String meterId);
}