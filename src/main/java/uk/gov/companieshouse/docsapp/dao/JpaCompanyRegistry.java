package uk.gov.companieshouse.docsapp.dao;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.docsapp.model.company.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "companieshouse.docsapp.test", havingValue = "false", matchIfMissing = true)
public class JpaCompanyRegistry implements CompanyRegistry {

    private CompanyRepository repo;

    final Random companyNumberGenerator = new Random();

    public JpaCompanyRegistry(CompanyRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Company> getCompanies(String namePattern, Integer yearOfIncorporation, Boolean activeState, Type companyType, Sort sortBy, Integer pageNumber) {
        org.springframework.data.domain.Sort sort = switch (sortBy) {
            case NUMBER -> org.springframework.data.domain.Sort.by("registrationNumber");
            case DATE -> org.springframework.data.domain.Sort.by("incorporatedOn");
            case NAME -> org.springframework.data.domain.Sort.by("companyName");
            case null -> null;
        };
        Pageable firstPageWithTwoElements = PageRequest.of(pageNumber, 3);

        Pageable secondPageWithFiveElements = PageRequest.of(1, 5);

        Page <Company> companies = repo.findAll(firstPageWithTwoElements);
        System.out.println(companies.getTotalElements() +  "THIS IS THE TOTAL NUMBER OF ELEMENTS");
        System.out.println(companies.getTotalPages() + "THIS  is THE TOTAL NUMBER OF PAGES ");
        return companies.stream().filter(company -> {
            boolean keep = true;
            if (namePattern != null && !company.getCompanyName().matches(namePattern)) keep = false;
            if (yearOfIncorporation != null && company.getIncorporatedOn().getYear() != yearOfIncorporation) keep = false;
            if (activeState != null && company.isActive() != activeState) keep = false;
            if (companyType != null) {
                Class<? extends Company> companyClass = switch (companyType) {
                    case LLP -> LimitedLiabilityPartnership.class;
                    case LTD -> LimitedCompany.class;
                    case FOREIGN -> ForeignCompany.class;
                    case NONPROFIT -> NonProfitOrganization.class;
                };
                if (!companyClass.isAssignableFrom(Company.class)) keep = false;
            }
            return keep;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Company getCompany(String number) {
        if (!repo.existsById(number)) {
            throw new IllegalArgumentException("Company not found");
        }
        return repo.getReferenceById(number);
    }

    @Override
    public void deleteCompany(String number) {
        if (!repo.existsById(number)) {
            throw new IllegalArgumentException("Company not found");
        }
        repo.deleteById(number);
    }

    @Override
    public Company addCompany(Company data) {

        String number;
        do {
            number = String.valueOf(companyNumberGenerator.nextInt());
        } while (repo.existsById(number));
        data.setRegistrationNumber(number);
        data.setIncorporatedOn(LocalDate.now());

        // TODO generate company number
        // TODO set the incorporation date to today
        return repo.save(data);
    }

    @Override
    public void editCompany(String number, Company data) {
        if (!repo.existsById(number)) {
            throw new IllegalArgumentException("Company not found");
        }
        Company company = repo.findById(number).get();
        company.setCompanyName(data.getCompanyName());
        company.setActive(data.isActive());
        company.setIncorporatedOn(data.getIncorporatedOn());
        company.setRegisteredAddress(data.getRegisteredAddress());

        // TODO force the registration number in the company to be the same as the number given
        repo.save(company);
    }

    @Override
    public void patchCompany(String number, Company data) {
        Optional<Company> maybeCompany = repo.findById(number);
        if (maybeCompany.isEmpty()) {
            throw new IllegalArgumentException("Company not found");
        } else {
            Company company = maybeCompany.get();
            if (!company.getClass().isAssignableFrom(data.getClass())) {
                throw new IllegalArgumentException("Incompatible data type");
            }
            if (data.getCompanyName() != null) company.setCompanyName(data.getCompanyName());
            if (data.isActive() != null) company.setActive(data.isActive());
            if (data.getIncorporatedOn() != null) company.setIncorporatedOn(data.getIncorporatedOn());
            if (data.getRegisteredAddress() != null) company.setRegisteredAddress(data.getRegisteredAddress());
            switch (company) {
                case ForeignCompany fc -> {
                    fc.setCountryOfOrigin(((ForeignCompany) data).getCountryOfOrigin());
                }
                case LimitedCompany ltd -> {
                    ltd.setNumberOfShares(((LimitedCompany) data).getNumberOfShares());
                    ltd.setPublic(((LimitedCompany) data).isPublic());
                }
                case LimitedLiabilityPartnership llp -> {
                    llp.setNumberOfPartners(((LimitedLiabilityPartnership) data).getNumberOfPartners());
                }
                case NonProfitOrganization nop -> {
                }
                default -> throw new IllegalArgumentException("Incorrect data type");
            }
            repo.save(company);
        }
    }
}
