package com.pug.partner.presenter.rest.dto;

import java.util.UUID;

public record RegisterPartnerEntityRequest(String cnpj, String name, UUID cityId, String address) {}
