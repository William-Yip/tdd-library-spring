package com.springtdd.tddspringlbrary.api.resource.model.repository;

import com.springtdd.tddspringlbrary.api.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro com isbn já utilizado por outro")
    public void existsIsbn() {
        // cenario
        Book book = Book.builder().title("titulo").author("autor").isbn("123").build();
        entityManager.persist(book);
        // execucao
        boolean exists = repository.existsByIsbn(book.getIsbn());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando nao existir um livro com isbn já utilizado por outro")
    public void doesntExistsIsbn() {
        String isbn = "321";
        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBook() {
        Book book = Book.builder().title("titulo").author("autor").isbn("123").build();
        Book savedBook = repository.save(book);
        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void shouldDeleteAnBook() {
        Book book = Book.builder().isbn("123").title("titulo").author("autor").build();
        entityManager.persist(book);
        Book savedBook= entityManager.find(Book.class, book.getId());
        repository.delete(savedBook);
        Book deletedBook = entityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isNull();
    }

}
