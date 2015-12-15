package example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.Application;
import example.domain.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Random;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
@Profile("test")
public class ProductControllerTest {

    private static final String BASE_URL = "/v1/product";

    private static final String RESOURCE_LOCATION_PATTERN = "http://localhost/v1/hotels/[0-9]+";

    @InjectMocks
    private ProductController productController;


    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }


    @Test
    public void shouldCreateRetrieveDelete() throws Exception {
        Product product = mockProduct("shouldCreateRetrieveDelete");
        byte[] productJson = toJson(product);

        //create
        MvcResult result = mockMvc.perform(post(BASE_URL)
                .content(productJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                //.andExpect(redirectedUrlPattern(RESOURCE_LOCATION_PATTERN))
                .andReturn();
        Long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

        //retrieve
        mockMvc.perform(get(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.description", is(product.getDescription())))
                .andExpect(jsonPath("$.price", is(product.getPrice())));

        //delete
        mockMvc.perform(delete(BASE_URL + "/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //retrieve should fail
        mockMvc.perform(get(BASE_URL + "/" + 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void shouldCreateUpdateDelete() throws Exception {
        Product product1 = mockProduct("shouldCreateUpdateDelete1");
        byte[] product1Json = toJson(product1);

        //create
        MvcResult result = mockMvc.perform(post(BASE_URL)
                .content(product1Json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                //.andExpect(redirectedUrlPattern(RESOURCE_LOCATION_PATTERN))
                .andReturn();
        Long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());


        //update
        Product product2 = mockProduct("shouldCreateUpdateDelete2");
        product2.setId(id);
        byte[] product2Json = toJson(product2);

        mockMvc.perform(put(BASE_URL + "/" + id)
                .content(product2Json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((id.intValue()))))
                .andExpect(jsonPath("$.name", is(product2.getName())))
                .andExpect(jsonPath("$.description", is(product2.getDescription())))
                .andExpect(jsonPath("$.price", is(product2.getPrice())));

        //delete
        mockMvc.perform(delete(BASE_URL + "/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


    private Product mockProduct(String prefix) {
        Product product = new Product();
        product.setName(prefix.concat("_name"));
        product.setDescription(prefix.concat("_description"));
        product.setPrice(new Random().nextDouble() * 100);
        return product;
    }

    private byte[] toJson(Object r) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(r).getBytes();
    }

    private long getResourceIdFromUrl(String locationUrl) {
        String[] parts = locationUrl.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }


}
