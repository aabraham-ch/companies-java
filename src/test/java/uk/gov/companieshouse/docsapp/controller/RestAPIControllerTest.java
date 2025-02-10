package uk.gov.companieshouse.docsapp.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.companieshouse.docsapp.dao.CompanyRegistry;
import uk.gov.companieshouse.docsapp.model.company.Company;
import uk.gov.companieshouse.docsapp.model.company.LimitedCompany;
import uk.gov.companieshouse.docsapp.model.company.LimitedLiabilityPartnership;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class RestAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Mock
    private CompanyRegistry companyRegistry;


    @Test
    void addCompany() throws Exception {
        Company newCompany = new LimitedLiabilityPartnership("New Company", true);
        String json = mapper.writeValueAsString(newCompany);
        System.out.println(json + "++++++++++=");
        this.mockMvc.perform(post("/companies")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void listCompany() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        List <Company> companies = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Company>>() {});


        assertEquals ( 4, companies.size());
    }

//    @Test
//    void getCompany() throws Exception {
//        MvcResult result = this.mockMvc.perform(get("/companies/194745294"))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andReturn();
//        List <Company> companies = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Company>>() {});
//
//    }

    @Test
    void deleteCompany() throws Exception {
        this.mockMvc.perform(delete("/companies/194745294"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void patchCompany () throws Exception {
        Company newCompany = new LimitedCompany("New Company", true);
        String json = mapper.writeValueAsString(newCompany);
        this.mockMvc.perform(patch("/companies/194745294")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());


    }

    @Test
    void patchNullCompany () throws Exception {
        Company newCompany = new LimitedCompany("New Company", true);
        String json = mapper.writeValueAsString(newCompany);
        this.mockMvc.perform(patch("/companies/19745294")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    void putCompany () throws Exception {
        Company newCompany = new LimitedCompany("New Company", false);
        String json = mapper.writeValueAsString(newCompany);
        this.mockMvc.perform(put("/companies/194745294")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void putNullCompany () throws Exception {
        Company newCompany = new LimitedCompany("New Company", true);
        String json = mapper.writeValueAsString(newCompany);
        this.mockMvc.perform(put("/companies/1994745294")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
    }


