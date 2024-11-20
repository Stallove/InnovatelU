package org.innovateiu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    public void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void testSaveNewDocument() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(DocumentManager.Author.builder().id("1").name("Author Name").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertEquals("Test Title", savedDocument.getTitle());
    }

    @Test
    void testUpdateExistingDocument() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Original Title")
                .content("Original Content")
                .author(DocumentManager.Author.builder().id("1").name("Author Name").build())
                .created(Instant.now())
                .build();

        String newId = documentManager.save(document).getId();

        DocumentManager.Document updatedDocument = DocumentManager.Document.builder()
                .id(newId)
                .title("Updated Title")
                .content("Updated Content")
                .author(DocumentManager.Author.builder().id("1").name("Author Name").build())
                .created(Instant.now())
                .build();

        documentManager.save(updatedDocument);

        Optional<DocumentManager.Document> retrievedDocument = documentManager.findById(newId);
        assertTrue(retrievedDocument.isPresent());
        assertEquals("Updated Title", retrievedDocument.get().getTitle());
        assertEquals("Updated Content", retrievedDocument.get().getContent());
    }

    @Test
    void testFindById() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Find Me")
                .content("Some Content")
                .author(DocumentManager.Author.builder().id("1").name("Author Name").build())
                .created(Instant.now())
                .build();

        String newId = documentManager.save(document).getId();

        Optional<DocumentManager.Document> foundDocument = documentManager.findById(newId);
        assertTrue(foundDocument.isPresent());
        assertEquals("Find Me", foundDocument.get().getTitle());
    }

    @Test
    void testSearchByTitlePrefix() {
        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .title("Alpha Document")
                .content("Content A")
                .author(DocumentManager.Author.builder().id("1").name("Author A").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .title("Beta Document")
                .content("Content B")
                .author(DocumentManager.Author.builder().id("2").name("Author B").build())
                .created(Instant.now())
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Alpha"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);
        assertEquals(1, results.size());
        assertEquals("Alpha Document", results.get(0).getTitle());
    }

    @Test
    void testSearchByAuthorId() {
        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .title("Document 1")
                .content("Content 1")
                .author(DocumentManager.Author.builder().id("1").name("Author A").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .title("Document 2")
                .content("Content 2")
                .author(DocumentManager.Author.builder().id("2").name("Author B").build())
                .created(Instant.now())
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .authorIds(List.of("1"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);
        assertEquals(1, results.size());
        assertEquals("Document 1", results.get(0).getTitle());
    }

    @Test
    void testSearchByCreatedDateRange() {
        Instant now = Instant.now();
        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .title("Old Document")
                .content("Content 1")
                .author(DocumentManager.Author.builder().id("1").name("Author A").build())
                .created(now.minusSeconds(3600))
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .title("New Document")
                .content("Content 2")
                .author(DocumentManager.Author.builder().id("2").name("Author B").build())
                .created(now)
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .createdFrom(now.minusSeconds(1800))
                .createdTo(now.plusSeconds(1800))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);
        assertEquals(1, results.size());
        assertEquals("New Document", results.get(0).getTitle());
    }
}