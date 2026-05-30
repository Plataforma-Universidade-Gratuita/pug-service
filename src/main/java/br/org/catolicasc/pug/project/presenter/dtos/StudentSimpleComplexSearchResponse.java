package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;

public record StudentSimpleComplexSearchResponse(
    AccountSimpleComplexSearchResponse account,
    String academicRegistration,
    CampusResponse campus) {}
