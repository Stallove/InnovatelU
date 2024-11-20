package org.innovateiu;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {
    private final Map<String, Document> documentStore = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || !documentStore.containsKey(document.getId())) {
            //ATTENTION class full managing ids not allowing creating ids by users
            document.setId(UUID.randomUUID().toString());
        }
        documentStore.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documentStore.values().stream()
                .filter(document -> matchDocumentToSearchRequest(document, request))
                .toList();
    }

    private boolean matchDocumentToSearchRequest(Document document, SearchRequest request) {
        return request == null
                || (matchesTitlePrefixes(document, request)
                && matchesContent(document, request)
                && matchesAuthorIds(document, request)
                && matchesCreatedFrom(document, request)
                && matchesCreatedTo(document, request));
    }

    private boolean matchesTitlePrefixes(Document document, SearchRequest request) {
        return request.getTitlePrefixes() == null
                || request.getTitlePrefixes().isEmpty()
                || request.getTitlePrefixes().stream().anyMatch(
                prefix -> document.getTitle()
                        != null && document.getTitle().startsWith(prefix)
        );
    }

    private boolean matchesContent(Document document, SearchRequest request) {
        return request.getContainsContents() == null
                || request.getContainsContents().isEmpty()
                || request.getContainsContents().stream().anyMatch(
                content -> document.getContent() != null
                        && document.getContent().contains(content)
        );
    }

    private boolean matchesAuthorIds(Document document, SearchRequest request) {
        return request.getAuthorIds() == null
                || request.getAuthorIds().isEmpty()
                || request.getAuthorIds().contains(document.getAuthor().getId());
    }

    private boolean matchesCreatedFrom(Document document, SearchRequest request) {
        return request.getCreatedFrom() == null
                || (document.getCreated() != null
                    && document.getCreated().isAfter(request.getCreatedFrom()));
    }

    private boolean matchesCreatedTo(Document document, SearchRequest request) {
        return request.getCreatedTo() == null ||
                (document.getCreated() != null
                        && document.getCreated().isBefore(request.getCreatedTo()));
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documentStore.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}