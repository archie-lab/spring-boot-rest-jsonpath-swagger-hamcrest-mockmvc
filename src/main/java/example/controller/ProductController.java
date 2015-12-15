package example.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import example.domain.Product;
import example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/v1/product")
@Api(value = "products", description = "Products API")
public class ProductController {

    private static final String DEFAULT_PAGE_NUM = "0";
    private static final String DEFAULT_PAGE_SIZE = "100";

    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }


    @RequestMapping(method = RequestMethod.POST,
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a product", notes = "Returns the URL of the new product in the Location header and created product")
    public Product createProduct(@RequestBody Product product, HttpServletRequest request, HttpServletResponse response) {
        Product createdProduct = productService.createProduct(product);
        response.setHeader("Location", request.getRequestURL().append("/").append(createdProduct.getId()).toString());
        return createdProduct;
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a product by id", notes = "You have to provide a valid product ID.")
    public
    @ResponseBody
    Product getProduct(@ApiParam(value = "The ID of the existing product", required = true) @PathVariable("id") Long id) {
        Product product = productService.getProduct(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        return product;
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    @ApiOperation(value = "Update a product",
            notes = "You have to provide a valid product ID in the URL and in the payload. The ID attribute can not be updated.")
    public Product updateProduct(@ApiParam(value = "The ID of the existing product", required = true) @PathVariable("id") Long id,
                                 @RequestBody Product product) {
        //TODO check IDs
        return productService.updateProduct(product);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@ApiParam(value = "The ID of the existing product", required = true) @PathVariable("id") Long id) {
        productService.deleteProduct(id);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a paginated list of products",
            notes = "The list is paginated. You can provide a page number (default 0) and a page size (default 100)")
    public
    @ResponseBody
    Page<Product> getAllProducts(@ApiParam(value = "The page number (zero-based)", required = true)
                                 @RequestParam(value = "page", required = true, defaultValue = DEFAULT_PAGE_NUM) Integer page,
                                 @ApiParam(value = "The page size", required = true)
                                 @RequestParam(value = "size", required = true, defaultValue = DEFAULT_PAGE_SIZE) Integer size) {
        return productService.getAllProducts(page, size);
    }
}
