package ru.itmo.wp.model.service;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.ArticleRepository;
import ru.itmo.wp.model.repository.impl.ArticleRepositoryImpl;

public class ArticleService {
    private final ArticleRepository articleRepository = new ArticleRepositoryImpl();

    public void validateArticle(Article article) throws ValidationException {
        if (Strings.isNullOrEmpty(article.getText())) {
            throw new ValidationException("Text can't be empty");
        }
        if (Strings.isNullOrEmpty(article.getTitle())) {
            throw new ValidationException("Title can't be empty");
        }
        if (article.getText().length() > 1000) {
            throw new ValidationException("Text is too long (>1000 characters)");
        }

        if (article.getTitle().length() > 255) {
            throw new ValidationException("Title is too long (>255 characters)");
        }

    }

    public void toggleHidden(long articleId, boolean value) {
        Article article = articleRepository.find(articleId);

        if (article != null) {
            articleRepository.updateHidden(articleId, value);
        }
    }
}
