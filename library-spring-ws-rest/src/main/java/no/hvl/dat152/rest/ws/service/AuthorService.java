/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import no.hvl.dat152.rest.ws.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;

/**
 * @author tdoy
 */
@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;

	// link to BookRepository is needed for the deleteById method.
	@Autowired
	private BookRepository bookRepository;
		
	
	public Author findById(int id) throws AuthorNotFoundException {
		
		Author author = authorRepository.findById(id)
				.orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));
		
		return author;
	}
	
	// TODO public saveAuthor(Author author)
	public Author saveAuthor(Author author){
		return authorRepository.save(author);
	}
		
	
	// TODO public Author updateAuthor(Author author, int id)
	public Author updateAuthor(Author author, int id) throws AuthorNotFoundException {

		Author updated = findById(id);

		updated.setFirstname(author.getFirstname());
		updated.setLastname(author.getLastname());

		return authorRepository.save(updated);
	}
	
	// TODO public List<Author> findAll()
	public List<Author> findAll(){
		return (List<Author>) authorRepository.findAll();
	}
	
	// TODO public void deleteById(int id) throws AuthorNotFoundException 
	public void deleteById(int id) throws AuthorNotFoundException{
		Author author = findById(id);

		// This one was a little tricky.
		// A book owns the link to authors, but each author has a Set of books they have written.
		// So for each book I first, delete author from the Set<Author> authors (locally)
		// Then I use bookRepository to update the book in the database (without this author).
		for (Book book : author.getBooks()) {
			book.removeAuthor(author);
			bookRepository.save(book);
		}
		// After that is done for every book in the Authors "Set<Book> books", I can safely delete the author from the database.

		authorRepository.delete(author);
	}
	
	// TODO public Set<Book> findBooksByAuthorId(int id)
	public Set<Book> findBooksByAuthorId(int id) throws AuthorNotFoundException {
		Author author = findById(id);
		return author.getBooks();
	}
}
