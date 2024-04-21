package com.voda.eip;

import com.eip.data.entity.MilkCollect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
public class Converter {

    public static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
        }
        return objectMapper;
    }

    /**
     * convert msg from internal broker to MilkCollect
     * @param msg
     *  4;010124-01-B-AF;1111ABCD;Nguy?n Van A;1111ABCD_01;4.7;30;25.3;Processing;4/20/2024
     * @return
     */

    public static MilkCollect messageToDTO(String msg) {

        log.info(" msg to convert: " + msg);
        MilkCollect milkCollect = new MilkCollect();
//        log.info("----------------- milkCollect {}", milkCollect);
        try {

            String[] ss = msg.split(";");

            log.info(" length: " + ss.length);
            for (int i = 0; i < ss.length; i++) {
                log.info(ss[i] + "--");
            }
//            log.info("----------------- milkCollect {}", milkCollect);
            if (ss.length < 10) {
                return null;
            }
//            log.info("----01------------- milkCollect {}", milkCollect);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
//            log.info("----0------------- milkCollect {}", milkCollect);
//            milkCollect.setId(Long.valueOf(ss[0]));

            milkCollect = MilkCollect.builder()
                    .id(Long.valueOf(ss[0]))
                    .serialWeigher(ss[1])
                    .codeSeller(ss[2])
                    .nameSeller(ss[3])
                    .codeTankSeller(ss[4])
                    .tankTareWeight(Double.parseDouble(ss[5]))
                    .tankGrossWeight(Double.parseDouble(ss[6]))
                    .tankNetWeght(Double.parseDouble(ss[7]))
                    .mqttStatus(ss[8])
                    .createdAt(ZonedDateTime.parse(ss[9], formatter))
//                    .createdAt(ZonedDateTime.parse("2024-04-20 18:51:00", formatter))
                    .build();
////            log.info("----1------------- milkCollect {}", milkCollect);
//            milkCollect.setSerialWeigher(ss[1]);
////            log.info("-----2------------ milkCollect {}", milkCollect);
//            milkCollect.setCodeSeller(ss[2]);
////            log.info("------3----------- milkCollect {}", milkCollect);
//            milkCollect.setNameSeller(ss[3]);
////            log.info("-------4---------- milkCollect {}", milkCollect);
//            milkCollect.setCodeTankSeller(ss[4]);
////            log.info("--------5--------- milkCollect {}", milkCollect);
//            milkCollect.setTankTareWeight(Double.parseDouble(ss[5]));
////            log.info("---------6-------- milkCollect {}", milkCollect);
//            milkCollect.setTankGrossWeight(Double.parseDouble(ss[6]));
////            log.info("----------7------- milkCollect {}", milkCollect);
//            milkCollect.setTankNetWeght(Double.parseDouble(ss[7]));
////            log.info("-----------8------ milkCollect {}", milkCollect);
//            milkCollect.setMqttStatus(ss[8]);
////            log.info("------------9----- milkCollect {}", milkCollect);
//            milkCollect.setCreatedAt(ZonedDateTime.parse(ss[9], formatter));
            log.info("-------------10---- milkCollect {}", milkCollect);

        } catch (Exception e) {
            log.info(" ----------- EX: -----------");
            log.info(e.getMessage());
            log.error(e.getMessage());
        }
//        log.info("------------11----- milkCollect {}", milkCollect);
        return  milkCollect;
    }


}
