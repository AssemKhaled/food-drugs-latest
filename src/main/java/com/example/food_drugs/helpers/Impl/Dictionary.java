package com.example.food_drugs.helpers.Impl;

import com.example.food_drugs.dto.Request.RangeInTempAndHum;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Dictionary {

    public String Type(String type){
//        String storing;
        if (type.equals("SCD1")){
            return  "Drugs: Frozen";
        } else if (type.equals("SCD2")) {
            return "Drugs: Chilled";
        } else if (type.equals("SCD3")) {
            return "Drugs: Room Temperature";
        } else if (type.equals("SCC1")) {
            return "Cosmetics: Room Temperature";
        } else if (type.equals("SCM1")) {
            return "Medical Equipment and Products: Frozen";
        } else if (type.equals("SCM2")) {
            return "Medical Equipment and Products: Chilled";
        } else if (type.equals("SCM3")) {
            return "Medical Equipment and Products: Cold Storage";
        } else if (type.equals("SCM4")) {
            return "Medical Equipment and Products: Room Temperature";
        } else if (type.equals("SCM5")) {
            return "Medical Equipment and Products: No Heat Exposure";
        } else if (type.equals("SCF1")) {
            return "Food: Dry";
            
        } else if (type.equals("SCF2")) {
            return "Food: Chilled";
        } else if (type.equals("SCF3")) {
            return "Food: Chilled Vegetables and Fruits";
        } else if (type.equals("SCF4")) {
            return "Food: Frozen";
        } else if (type.equals("SCA1")) {
            return "Fodder(Animal Feed): Fodder";
        }
        else if (type.equals("SCP1")){
            return "Pesticides: Pesticides";
        }
        else {
         return "NOT FOUND";
        }
    }

    public RangeInTempAndHum RangeInTempAndHumValid(String storingCategory ,Double temp ,Double hum){
        Map<String ,RangeInTempAndHum> result = new HashMap<>();
        //ANY VALUE = 10000 OR -10000 MEANS THAT VALUE IS NULL
        result.put("SCD1",RangeInTempAndHum
                .builder()
                .storingCategory("Drugs: Frozen")
                .maxTemp(-10D)
                .minTemp(-20D)
                .maxHum(10000D)
                .minHum(-10000D)
                .build());
        result.put("SCD2",RangeInTempAndHum
                .builder()
                .storingCategory("Drugs: Chilled")
                .maxTemp(8D)
                .minTemp(2D)
                .maxHum(10000D)
                .minHum(-10000D)
                .build());
        result.put("SCD3",RangeInTempAndHum
                .builder()
                .storingCategory("Drugs: Room Temperature")
                .maxTemp(25D)
                .minTemp(-10000D)
                .maxHum(60D)
                .minHum(-10000D)
                .build());
        result.put("SCC1",RangeInTempAndHum
                .builder()
                .storingCategory("Cosmetics: Room Temperature")
                .maxTemp(25D)
                .minTemp(-10000D)
                .maxHum(60D)
                .minHum(-10000D)
                .build());
        result.put("SCM1",RangeInTempAndHum
                .builder()
                .storingCategory("Medical Equipment and Products: Frozen")
                .maxTemp(-10D)
                .minTemp(-20D)
                .maxHum(10000D)
                .minHum(-10000D)
                .build());
        result.put("SCM2",RangeInTempAndHum
                .builder()
                .storingCategory("Medical Equipment and Products: Chilled")
                .maxTemp(8D)
                .minTemp(2D)
                .maxHum(10000D)
                .minHum(-10000D)
                .build());
        result.put("SCM3",RangeInTempAndHum
                .builder()
                .storingCategory("Medical Equipment and Products: Cold Storage")
                .maxTemp(15D)
                .minTemp(8D)
                .maxHum(60D)
                .minHum(-10000D)
                .build());
        result.put("SCM4",RangeInTempAndHum
                .builder()
                .storingCategory("Medical Equipment and Products: Room Temperature")
                .maxTemp(30D)
                .minTemp(15D)
                .maxHum(60D)
                .minHum(-10000D)
                .build());
        result.put("SCM5",RangeInTempAndHum
                .builder()
                .storingCategory("Medical Equipment and Products: No Heat Exposure")
                .maxTemp(41D)
                .minTemp(-10000D)
                .maxHum(10000D)
                .minHum(-10000D)
                .build());
        result.put("SCF1",RangeInTempAndHum
                .builder()
                .storingCategory("Food: Dry")
                .maxTemp(26D)
                .minTemp(-10000D)
                .maxHum(60D)
                .minHum(-10000D)
                .build());
        result.put("SCF2",RangeInTempAndHum
                .builder()
                .storingCategory("Food: Chilled")
                .maxTemp(10D)
                .minTemp(-1.5)
                .maxHum(90D)
                .minHum(75D)
                .build());
        result.put("SCF3",RangeInTempAndHum
                .builder()
                .storingCategory("Food: Chilled Vegetables and Fruits")
                .maxTemp(21D)
                .minTemp(-1.5)
                .maxHum(95D)
                .minHum(85D)
                .build());
        result.put("SCF4",RangeInTempAndHum
                .builder()
                .storingCategory("Food: Frozen")
                .maxTemp(-19D)
                .minTemp(-10000D)
                .maxHum(99D)
                .minHum(75D)
                .build());
        result.put("SCA1",RangeInTempAndHum
                .builder()
                .storingCategory("Fodder(Animal Feed): Fodder")
                .maxTemp(31D)
                .minTemp(-10000D)
                .maxHum(61D)
                .minHum(-10000D)
                .build());
        result.put("SCP1",RangeInTempAndHum
                .builder()
                .storingCategory("Pesticides: Pesticides")
                .maxTemp(36D)
                .minTemp(-10000D)
                .maxHum(10000D)
                .minHum(-10000D)
                .build());
        if (storingCategory != null) {
            RangeInTempAndHum rangeInTempAndHum = result.get(storingCategory);
            if (rangeInTempAndHum != null) {
                if (temp != null) {
                    if (rangeInTempAndHum.getMaxTemp() > temp && temp > rangeInTempAndHum.getMinTemp()) {
                        rangeInTempAndHum.setInRangeTemp(true);
                    }else {
                        rangeInTempAndHum.setInRangeTemp(false);
                    }
                }
                if (hum != null) {
                    if (rangeInTempAndHum.getMaxHum() > hum && hum > rangeInTempAndHum.getMinHum()) {
                        rangeInTempAndHum.setInRangeHum(true);
                    }
                    else {
                        rangeInTempAndHum.setInRangeHum(false);
                    }
                }

            return rangeInTempAndHum;
            }
            return null;
        }else
        {
            return null;
        }

    }
}
