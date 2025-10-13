package com.pug.partner.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.partner.domain.Address;
import com.pug.partner.domain.Cnpj;
import com.pug.partner.domain.PartnerEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PartnerEntityMapperTest {

  @Test
  void mapsBothWays() {
    var id = UUID.randomUUID();
    var city = UUID.randomUUID();
    var d =
        PartnerEntity.newActive()
            .id(id)
            .cnpj(Cnpj.of("19131243000197"))
            .name("Org")
            .cityId(city)
            .address(Address.of("Av Y, 456"))
            .build();

    var e = PartnerEntityMapper.toEntity(d);
    assertEquals(id, e.getId());
    assertEquals(Cnpj.of("19.131.243/0001-97"), e.getCnpj());
    assertEquals("Org", e.getName());
    assertEquals(city, e.getCityId());
    assertEquals("Av Y, 456", e.getAddress());
    assertTrue(e.isActive());

    var d2 = PartnerEntityMapper.toDomain(e);
    assertEquals(d, d2);
  }
}
