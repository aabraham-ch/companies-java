package uk.gov.companieshouse.docsapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uk.gov.companieshouse.docsapp.dao.CompanyRegistry;
import uk.gov.companieshouse.docsapp.dao.InMemoryCompanyRegistry;
import uk.gov.companieshouse.docsapp.model.company.Company;

import java.util.List;

@RestController
public class RestAPIController {


    private  CompanyRegistry  companyList;

    public RestAPIController (CompanyRegistry companyRegistry) {
        this.companyList = companyRegistry;
    }

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> listCompany(@RequestParam (required = false) String namePattern,
                                                     @RequestParam (required = false) Integer yearOfIncorporation,
                                                     @RequestParam (required = false) Boolean activeStatus,
                                                     @RequestParam (required = false) CompanyRegistry.Type companyType,
                                                     @RequestParam (required = false) CompanyRegistry.Sort sortBy,
                                                     @RequestParam (required = true) Integer pageNumber)
                                                    {

        System.out.println(yearOfIncorporation + "???????????" + companyType);
        List<Company> companies = companyList.getCompanies( namePattern, yearOfIncorporation, activeStatus, companyType, sortBy, pageNumber);

        if (companies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }


        return ResponseEntity.ok(companies);
    }

    @GetMapping("/companies/{companyNum}")
    public ResponseEntity<Company> getCompany(@PathVariable (required = true) String companyNum) {
        Company company = companyList.getCompany(companyNum);

        if (company == null) {

            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(company);
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createNewCompany(@RequestBody Company companyData) {
        if (companyData.getIncorporatedOn() != null || companyData.getRegistrationNumber() != null) {
            return ResponseEntity.badRequest().build();
        }
        Company newCompany = companyList.addCompany(companyData);
        return ResponseEntity.ok(newCompany);
    }


    @PutMapping("/companies/{companyNum}")
    public ResponseEntity<Void> putCompany(@PathVariable (required = true) String companyNum, @RequestBody Company companyData) {
        Company company = companyList.getCompany(companyNum);

        if (company == null) {
            return ResponseEntity.notFound().build();
        }

        companyList.editCompany(companyNum, companyData);

        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/companies/{companyNum}")
    public ResponseEntity<Company> patchCompany(@PathVariable (required = true) String companyNum, @RequestBody Company companyData) {
        Company company = companyList.getCompany(companyNum);

        if (company == null) {
            return ResponseEntity.notFound().build();
        }

        companyList.patchCompany(companyNum, companyData);

        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/companies/{companyNum}")
    public ResponseEntity<Company> deleteCompany(@PathVariable (required = true) String companyNum) {
        Company company = companyList.getCompany(companyNum);

        if (company == null) {
            return ResponseEntity.notFound().build();
        }

        companyList.deleteCompany(companyNum);

        return ResponseEntity.noContent().build();

    }

}
