package br.org.catolicasc.pug.partner.service;

import br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.service.dtos.StaffComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/** Application service interface dedicated exclusively to querying Staff data. */
public interface StaffReadService {

  StaffView getViewByAccountId(UUID accountId);

  List<StaffView> listViews();

  List<StaffView> listViewsByIds(List<UUID> ids);

  PageResult<StaffComplexSearchView> search(
      PageQuery pageQuery, StaffComplexSearchCriteria criteria);
}
