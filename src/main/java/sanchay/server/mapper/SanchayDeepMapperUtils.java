package sanchay.server.mapper;

import org.modelmapper.ModelMapper;

import java.util.Map;
import java.util.stream.Collectors;

public class SanchayDeepMapperUtils {

    public static <S, T> Map<String, T> convertMap(Map<String, S> sourceMap, Class<T> targetClass,
                                                   ModelMapper modelMapper) {
        return sourceMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        (entry) -> entry.getKey(),
                        (entry) -> modelMapper.map(entry.getValue(), targetClass)
                ));
    }
}
