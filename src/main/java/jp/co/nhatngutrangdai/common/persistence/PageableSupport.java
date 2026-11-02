package jp.co.nhatngutrangdai.common.persistence;

import java.util.Set;

import jp.co.nhatngutrangdai.common.exception.ApiExceptionFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableSupport {

    private PageableSupport() {
    }

    public static Pageable validated(Pageable pageable, Set<String> allowedProperties, String defaultProperty) {
        for (Sort.Order order : pageable.getSort()) {
            if (!allowedProperties.contains(order.getProperty())) {
                throw ApiExceptionFactory.badRequest("Unsupported sort field: " + order.getProperty());
            }
        }

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.ASC, defaultProperty);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
