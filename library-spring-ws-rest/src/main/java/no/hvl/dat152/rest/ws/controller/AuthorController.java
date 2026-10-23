/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.service.AuthorService;

/**
 * 
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class AuthorController {

    @Autowired
    private AuthorService authorService;
	

    @GetMapping("/authors")
    public ResponseEntity<Object> getAllAuthors(){
        List<Author> authors = authorService.findAll();

        if(authors.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(authors, HttpStatus.OK);
    }


    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getAuthor(@PathVariable int id) throws AuthorNotFoundException{
        try{
            Author author = authorService.findById(id);
            return new ResponseEntity<>(author, HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/authors/{id}/books")
    public ResponseEntity<Object> getBooksByAuthorId(@PathVariable int id) throws AuthorNotFoundException {
        Set<Book> books = authorService.findBooksByAuthorId(id);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }



    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@RequestBody Author author){
        Author nauthor = authorService.saveAuthor(author);

        return new ResponseEntity<>(nauthor, HttpStatus.CREATED);
    }
	

    @PutMapping("/authors/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable int id,
                                               @RequestBody Author author) throws AuthorNotFoundException {
        try{
            Author updated = authorService.updateAuthor(author, id);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
