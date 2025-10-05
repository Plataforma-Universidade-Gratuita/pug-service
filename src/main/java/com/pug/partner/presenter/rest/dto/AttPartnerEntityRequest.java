package com.pug.partner.presenter.rest.dto;

import java.util.UUID;

/** Partial update. Null fields are ignored. */
public record AttPartnerEntityRequest(String cnpj, String name, UUID cityId, String address) {}
