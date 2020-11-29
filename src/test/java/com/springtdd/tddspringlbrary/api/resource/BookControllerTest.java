package com.springtdd.tddspringlbrary.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springtdd.tddspringlbrary.api.entity.Book;
import com.springtdd.tddspringlbrary.api.resource.dto.BookDTO;
import com.springtdd.tddspringlbrary.api.service.BookService;
import com.springtdd.tddspringlbrary.exceptions.BussinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookSuccessfully() throws Exception {

        BookDTO dto = BookDTO.builder().author("William").title("Biografia").isbn("123").build();
        Book savedBook = Book.builder().id(1l).author("William").title("Biografia").isbn("123").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("id").value(savedBook.getId()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar erros se receber um livro invalido")
    public void invalidBody() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());
        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(Book.builder().build());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("erros", hasSize(3)));
    }

    @Test
    @DisplayName("Deve retornar erro de logica de negocio(isbn duplicado)")
    public void duplicatedIsbn() throws Exception {

        String json = new ObjectMapper().writeValueAsString(BookDTO.builder().author("asda").title("au").isbn("123").build());
        String errorMessage = "Isbn duplicado";
        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BussinessException(errorMessage));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("erros", hasSize(1)))
                .andExpect(jsonPath("erros[0]").value(errorMessage));

    }

}
