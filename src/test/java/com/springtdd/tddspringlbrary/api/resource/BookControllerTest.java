package com.springtdd.tddspringlbrary.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springtdd.tddspringlbrary.api.entity.Book;
import com.springtdd.tddspringlbrary.api.resource.dto.BookDTO;
import com.springtdd.tddspringlbrary.api.service.BookService;
import com.springtdd.tddspringlbrary.exceptions.BussinessException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books/";

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

    @Test
    @DisplayName("Deve retornar as informações de um livro")
    public void getBookById() throws Exception {

        Book validBook = Book.builder().id(12l).title("Yeah").author("sla").isbn("321").build();

        BDDMockito.given(service.getById(validBook.getId())).willReturn(Optional.of(validBook));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + validBook.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);


        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(validBook.getId()))
                .andExpect(jsonPath("title").value(validBook.getTitle()))
                .andExpect(jsonPath("author").value(validBook.getAuthor()))
                .andExpect(jsonPath("isbn").value(validBook.getIsbn()));
    }


    @Test
    @DisplayName("Deve retornar not Found para um livro nao existente")
    public void returnNotFound() throws Exception {

        BDDMockito.when(service.getById(Mockito.anyLong())).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 12l))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar not Found para um livro nao existente")
    public void deleteABook() throws Exception {

        BDDMockito.when(service.getById(Mockito.anyLong())).thenReturn(Optional.of(Book.builder().id(12l).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 12l))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro para deleção")
    public void deleteInexistentBook() throws Exception {
        // @delete -> /api/book/12
        BDDMockito.when(service.getById(Mockito.anyLong())).thenReturn(Optional.empty());
        // deve retornar resource not found
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 12l))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar o livro atualizado")
    public void updateAnExistingBook() throws Exception {
        Long id = 12l;

        BookDTO updatingDTO = BookDTO.builder().id(id).author("updatedAuthor").title("updatedTitle").isbn("123").build();
        String updatingJson = new ObjectMapper().writeValueAsString(updatingDTO);
        Book oldBook = Book.builder().id(id).author("oldAuthor").title("oldTitle").isbn("123").build();

        BDDMockito.when(service.getById(id)).thenReturn(Optional.of(oldBook));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(updatingJson);

        mvc
                .perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("author").value(updatingDTO.getAuthor()))
                .andExpect(jsonPath("title").value(updatingDTO.getTitle()))
                .andExpect(jsonPath("isbn").value(updatingDTO.getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar erro quando for atualizar livro inexistente")
    public void updateAnInexistingBook() throws Exception {
        Long id = 12l;

        BookDTO updatingDTO = BookDTO.builder().id(id).author("updatedAuthor").title("updatedTitle").isbn("123").build();
        String updatingJson = new ObjectMapper().writeValueAsString(updatingDTO);

        BDDMockito.when(service.getById(id)).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(updatingJson);

        mvc
                .perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findBooksTest() throws Exception {
        long id = 1l;
        Book book = Book.builder()
                .id(id)
                .title(makeBookDTO().getTitle())
                .author(makeBookDTO().getAuthor())
                .isbn(makeBookDTO().getIsbn())
                .build();

        BDDMockito.given( service.find(Mockito.any(Book.class), Mockito.any(Pageable.class) ))
                .willReturn(new PageImpl<Book>( Arrays.asList(book), PageRequest.of(0, 100), 1) );

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));


    }

    public BookDTO makeBookDTO() {
        return BookDTO.builder().author("Autor").title("Titulo").isbn("123").build();
    }

}
