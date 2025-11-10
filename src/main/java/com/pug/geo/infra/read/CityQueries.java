package com.pug.geo.infra.read;

import com.pug.geo.infra.read.dtos.CityView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityQueries {
  Optional<CityView> findOptionalById(UUID id);

  Optional<CityView> findOptionalByIbgeCode(String ibgeCode);

  List<CityView> listAllCities();

  List<CityView> searchByName(String key);
}
