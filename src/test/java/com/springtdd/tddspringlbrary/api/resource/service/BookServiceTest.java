package com.springtdd.tddspringlbrary.api.resource.service;

import com.springtdd.tddspringlbrary.api.entity.Book;
import com.springtdd.tddspringlbrary.api.resource.model.repository.BookRepository;
import com.springtdd.tddspringlbrary.api.resource.service.impl.BookServiceImp;
import com.springtdd.tddspringlbrary.api.service.BookService;
import com.springtdd.tddspringlbrary.exceptions.BussinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class) // gera apenas emular o minimo do Spring para rodar os testes
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository; // apenas usamos o MockBean para o repository, pois estamos testando a classe de service e nao o repository em si

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImp(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveABook() {
        // cenario
        Book book = makeValidBook();
        Book bookWithId = Book.builder().id(1l).isbn("1234").title("titulo").author("oi").build();
        Mockito.when(repository.save(book)).thenReturn(bookWithId);

        // execucao
        Book savedBook = service.save(book);

        // verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Should throw an Exception for duplicated isbn")
    public void duplicatedIsbn() {
        // cenario
        String errorMessage = "Isbn duplicado";
        Book book = makeValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        // execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));
        // checagem
        assertThat(exception)
                .isInstanceOf(BussinessException.class)
                .hasMessage(errorMessage);

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    public Book makeValidBook() {
        Book book = Book.builder().isbn("1234").title("titulo").author("oi").build();
        return book;
    }


}
