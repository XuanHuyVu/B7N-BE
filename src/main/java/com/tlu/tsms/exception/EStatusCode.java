package com.tlu.tsms.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EStatusCode {
    SUCCESS(HttpServletResponse.SC_OK, "SYS_200", "success"),
    ERROR(HttpServletResponse.SC_BAD_REQUEST, "SYS_400_ERROR", "error"),
    NO_CONTENT(HttpServletResponse.SC_NO_CONTENT, "SYS_204", "no.content"),
    BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST, "SYS_400", "bad.request"),
    VALIDATION_ERROR(HttpServletResponse.SC_BAD_REQUEST, "SYS_401_VALID", "validation.error"),
    UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "SYS_401", "unauthorized"),
    FORBIDDEN(HttpServletResponse.SC_FORBIDDEN, "SYS_403", "forbidden"),
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "SYS_404", "not.found"),
    CONFLICT(HttpServletResponse.SC_BAD_REQUEST, "SYS_409", "conflict"),
    INTERNAL_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SYS_500", "internal.error"),
    UNKNOWN_ERROR(HttpServletResponse.SC_BAD_REQUEST, "000", "unknown.error"),
    INVALID_PARAMS(HttpServletResponse.SC_BAD_REQUEST, "001", "invalid.params"),
    DATA_NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "002", "data.not.found"),
    FORM_ERROR(HttpServletResponse.SC_BAD_REQUEST, "003", "form.error"),
    REST_ERROR(HttpServletResponse.SC_BAD_REQUEST, "004", "rest.error"),
    PROCESSING_APIKEY_ERROR(HttpServletResponse.SC_BAD_REQUEST, "005", "processing.apikey.error"),
    PROCESSING_JWT_ERROR(HttpServletResponse.SC_BAD_REQUEST, "006", "processing.jwt.error"),
    INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "008", "internal.server.error"),
    REQUIRED_PARAMS(HttpServletResponse.SC_BAD_REQUEST, "011", "required.params"),
    PATTERN_INVALID(HttpServletResponse.SC_BAD_REQUEST, "012", "pattern.invalid"),
    LENGTH_INVALID(HttpServletResponse.SC_BAD_REQUEST, "013", "length.invalid"),
    MIN_LENGTH_INVALID(HttpServletResponse.SC_BAD_REQUEST, "014", "min.length.invalid"),
    MAX_LENGTH_INVALID(HttpServletResponse.SC_BAD_REQUEST, "015", "max.length.invalid"),
    MIN_VALUE_INVALID(HttpServletResponse.SC_BAD_REQUEST, "016", "min.value.invalid"),
    MAX_VALUE_INVALID(HttpServletResponse.SC_BAD_REQUEST, "017", "max.value.invalid"),
    DROP_LIST_INVALID(HttpServletResponse.SC_BAD_REQUEST, "018", "drop.list.invalid"),
    OPTION_INVALID(HttpServletResponse.SC_BAD_REQUEST, "019", "option.invalid"),
    NOT_PERMISSION(HttpServletResponse.SC_FORBIDDEN, "020", "not.permission"),
    INVALID_STATUS(HttpServletResponse.SC_BAD_REQUEST, "021", "invalid.status"),
    HIDDEN_INVALID(HttpServletResponse.SC_BAD_REQUEST, "022", "hidden.invalid"),
    SCRIPT_INVALID(HttpServletResponse.SC_BAD_REQUEST, "023", "script.invalid"),
    ID_CARD_INVALID(HttpServletResponse.SC_BAD_REQUEST, "032", "id.card.invalid"),
    DATE_OF_BIRTH_INVALID(HttpServletResponse.SC_BAD_REQUEST, "033", "date.of.birth.invalid"),
    NAME_INVALID(HttpServletResponse.SC_BAD_REQUEST, "034", "name.invalid"),
    ID_CARD_INVALID_WITH_DOB(HttpServletResponse.SC_BAD_REQUEST, "035", "id.card.invalid.with.dob"),
    DISTRICT_INVALID_WITH_PROVINCE(HttpServletResponse.SC_BAD_REQUEST, "036", "district.invalid.with.province"),
    WARD_INVALID_WITH_DISTRICT(HttpServletResponse.SC_BAD_REQUEST, "037", "province.invalid.with.ward"),
    DATE_INVALID(HttpServletResponse.SC_BAD_REQUEST, "038", "date.invalid"),
    LOAD_PRIVATE_KEY_ERROR(HttpServletResponse.SC_NOT_FOUND, "082", "load.private.key.error"),
    LOAD_PUBLIC_KEY_ERROR(HttpServletResponse.SC_NOT_FOUND, "083", "load.public.key.error"),
    GENERATE_KEYPAIR_ERROR(HttpServletResponse.SC_BAD_REQUEST, "084", "gen.keypair.error"),
    LOAD_KEYPAIR_ERROR(HttpServletResponse.SC_NOT_FOUND, "085", "load.keypair.error"),
    SAVE_KEYPAIR_ERROR(HttpServletResponse.SC_BAD_REQUEST, "086", "save.keypair.error"),
    RSA_ENCRYPT_ERROR(HttpServletResponse.SC_BAD_REQUEST, "087", "rsa.encrypt.error"),
    RSA_DECRYPT_ERROR(HttpServletResponse.SC_BAD_REQUEST, "088", "rsa.decrypt.error"),
    INVALID_RULE(HttpServletResponse.SC_BAD_REQUEST, "090", "invalid.rule"),
    THIRD_PARTY_ERROR(HttpServletResponse.SC_BAD_REQUEST, "091", "third.party.error"),
    NOT_ENOUGH_FIELD_DECISION(HttpServletResponse.SC_BAD_REQUEST, "092", "not.enough.field.decision"),
    SOURCE_NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "001", "source.not.found"),
    PHONE_INVALID(HttpServletResponse.SC_BAD_REQUEST, "002", "phone.invalid"),
    EMAIL_INVALID(HttpServletResponse.SC_BAD_REQUEST, "003", "email.invalid"),
    ;

    private final int status;
    private final String code;
    private final String message;
}