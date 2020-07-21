package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service("trainingService")
public class TrainingServiceImpl implements TrainingService {

    /*
      this service will manage a complete re-train.
     */
    private final Integer interval;
    private final LocalDate startDate;

    @Autowired
    public TrainingServiceImpl(
            @Value("${interval}") Integer interval,
            @Value("${start.date}") String startDate
    ){
        this.interval = interval;
        this.startDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    @Override
    public void train() {

        /*

         TBC.

          trains all countries, collecting the data required by interval..generally it will be one year at a time.

          not required as of yet...


         */

    }
}
