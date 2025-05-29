package software.pxel.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.pxel.entity.EmailEntity;
import software.pxel.entity.PhoneEntity;
import software.pxel.entity.UserDocument;
import software.pxel.entity.UserEntity;
import software.pxel.repository.EmailRepository;
import software.pxel.repository.PhoneRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ElasticsearchClient elasticsearchClient;
    private final EmailRepository emailRepository;
    private final PhoneRepository phoneRepository;

    @Cacheable(value = "users", key = "#name + '_' + #email + '_' + #phone + '_' + #dateOfBirth + '_' + #page + '_' + #size")
    public List<UserDocument> searchUser(
            LocalDate dateOfBirth,
            String phone,
            String name,
            String email,
            int page,
            int size) throws IOException {

        List<Query> filters = new ArrayList<>();

        if (dateOfBirth != null) {
            filters.add(Query.of(q -> q
                    .range(r -> r
                            .date(d -> d
                                    .field("dateOfBirth")
                                    .gt(dateOfBirth.toString())
                            )
                    )
            ));
        }

        if (phone != null && !phone.isEmpty()) {
            filters.add(Query.of(q -> q
                    .term(t -> t
                            .field("phone")
                            .value(phone)
                    )));
        }

        if (name != null && !name.isEmpty()) {
            filters.add(Query.of(q -> q
                    .matchPhrasePrefix(mpp -> mpp
                            .field("name")
                            .query(name)
                    )));
        }

        if (email != null && !email.isEmpty()) {
            filters.add(Query.of(q -> q
                    .term(t -> t
                            .field("email")
                            .value(email)
                    )));
        }

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        boolQuery.filter(filters);

        SearchResponse<UserDocument> response = elasticsearchClient.search(s -> s
                        .index("users")
                        .query(q -> q.bool(boolQuery.build()))
                        .from(page * size)
                        .size(size),
                UserDocument.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "users", allEntries = true)
    public void addUserToIndex(UserEntity user) {
        UserDocument doc = new UserDocument();
        doc.setId(String.valueOf(user.getId()));
        doc.setName(user.getName());
        doc.setDateOfBirth(user.getDateOfBirth());

        List<EmailEntity> emails = emailRepository.findAllByUserEntity(user);
        List<PhoneEntity> phones = phoneRepository.findAllByUserEntity(user);

        if (!emails.isEmpty()) {
            doc.setEmail(emails.get(0).getEmail());
        }

        if (!phones.isEmpty()) {
            doc.setPhone(phones.get(0).getPhone());
        }

        IndexRequest<UserDocument> request = IndexRequest.of(i -> i
                .index("users")
                .id(doc.getId())
                .document(doc)
        );

        try {
            elasticsearchClient.index(request);
        } catch (IOException e) {
            throw new RuntimeException("Indexing error", e);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteUserFromIndex(UserEntity user) {
        try {
            elasticsearchClient.delete(d -> d
                    .index("users")
                    .id(String.valueOf(user.getId()))
            );
        } catch (IOException e) {
            throw new RuntimeException("Delete error", e);
        }
    }
}
