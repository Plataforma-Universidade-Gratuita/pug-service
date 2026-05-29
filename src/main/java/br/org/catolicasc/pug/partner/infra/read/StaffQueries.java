package br.org.catolicasc.pug.partner.infra.read;

import br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.service.dtos.StaffComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Read-only interface for executing staff profile queries. */
public interface StaffQueries {

  Optional<StaffView> findOptionalById(UUID id);

  List<StaffView> listAllByIds(List<UUID> ids);

  List<StaffView> listAllStaff();

  PageResult<StaffComplexSearchView> search(
      PageQuery pageQuery, StaffComplexSearchCriteria criteria);
}
