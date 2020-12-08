package com.springtdd.tddspringlbrary.api.resource.service;

import com.springtdd.tddspringlbrary.api.entity.Book;
import com.springtdd.tddspringlbrary.api.resource.model.repository.BookRepository;
import com.springtdd.tddspringlbrary.api.resource.service.impl.BookServiceImpl;
import com.springtdd.tddspringlbrary.api.service.BookService;
import com.springtdd.tddspringlbrary.exceptions.BussinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class) // gera apenas emular o minimo do Spring para rodar os testes
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository; // apenas usamos o MockBean para o repository, pois estamos testando a classe de service e nao o repository em si

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
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

    @Test
    @DisplayName("Deve retornar um livro pelo id")
    public void getBookById() {
        Long id = 12l;
        Book validBook = makeValidBook();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(validBook));
        Optional<Book> optional = service.getById(id);
        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getId()).isEqualTo(validBook.getId());
        assertThat(optional.get().getTitle()).isEqualTo(validBook.getTitle());
        assertThat(optional.get().getAuthor()).isEqualTo(validBook.getAuthor());
        assertThat(optional.get().getIsbn()).isEqualTo(validBook.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio quando obter um livro que nao existe")
    public void getAnInexistentBook() {
        Long id = 12l;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<Book> optional = service.getById(id);
        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar atualizar um livro inexistente")
    public void errorWhenUpdating() {
        Book book = new Book();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso")
    public void updateAnBook() {
        // cenario
        long id = 11l;

        // livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        //simulacao
        Book updatedBook = makeValidBook();
        updatedBook.setId(id);

        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        // execucao
        Book book = service.update(updatingBook);

        //verificacoes
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());

    }

    @Test
    @DisplayName("Deve filtrar um livro")
    public void filterABook() {
        // cenario
        Book book = makeValidBook();
        Page<Book> pageResult = new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100),1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(pageResult);
        List<Book> list = Arrays.asList(book);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Book> result = service.find(book, pageRequest);

        // execucao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(100);
    }

    public Book makeValidBook() {
        Book book = Book.builder().isbn("1234").title("titulo").author("oi").build();
        return book;
    }

}
