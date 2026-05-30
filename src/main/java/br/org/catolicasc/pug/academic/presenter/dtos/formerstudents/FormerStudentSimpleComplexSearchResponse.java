package br.org.catolicasc.pug.academic.presenter.dtos.formerstudents;

import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountSimpleComplexSearchResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;

public record FormerStudentSimpleComplexSearchResponse(
    AccountSimpleComplexSearchResponse account,
    String academicRegistration,
    CampusResponse campus) {}
