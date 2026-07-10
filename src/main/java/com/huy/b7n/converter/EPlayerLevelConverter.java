package com.huy.b7n.converter;

import com.huy.b7n.common.EPlayerLevel;
import com.huy.b7n.exception.EStatusCode;
import com.huy.b7n.utils.ErrorUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.logging.log4j.util.Strings;

import java.util.Optional;

@Converter
public class EPlayerLevelConverter implements AttributeConverter<EPlayerLevel, String> {

    @Override
    public String convertToDatabaseColumn(EPlayerLevel eAcademicRank) {
        return Optional.ofNullable(eAcademicRank)
                .map(EPlayerLevel::getLevel)
                .orElse(null);
    }

    @Override
    public EPlayerLevel convertToEntityAttribute(String source) {
        try {
            if (Strings.isBlank(source)) return null;
            return EPlayerLevel.lookup(source);
        } catch (IllegalArgumentException ex) {
            throw ErrorUtils.exception(EStatusCode.SOURCE_NOT_FOUND, "EPlayerLevel");
        }
    }
}

