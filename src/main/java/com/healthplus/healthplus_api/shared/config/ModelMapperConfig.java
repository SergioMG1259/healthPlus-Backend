package com.healthplus.healthplus_api.shared.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper ModelMapper() {

//        // Mapeo personalizado para la lista de alergias
//        modelMapper.addMappings(new PropertyMap<Patient, PatientDetailsDTO>() {
//            @Override
//            protected void configure() {
//                // Mapeo para convertir List<PatientAllergy> a List<Allergy>
//                using(allergiesConverter()).map(source.getAllergies(), destination.getAllergies());
//            }
//        });

        return new ModelMapper();
    }

    // Conversor personalizado para convertir la lista de PatientAllergy a Allergy
//    private Converter<List<PatientAllergy>, List<Allergy>> allergiesConverter() {
//        return context -> context.getSource().stream()
//                .map(PatientAllergy::getAllergy)
//                .collect(Collectors.toList());
//    }
}
