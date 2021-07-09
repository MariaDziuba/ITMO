package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.ArticleRepository;
import ru.itmo.wp.model.repository.impl.ArticleRepositoryImpl;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** @noinspection unused*/
public class ArticlePage extends Page {
    private final ArticleRepository articleRepository = new ArticleRepositoryImpl();
    private final ArticleService articleService = new ArticleService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        if (getUser() == null) {
            throw new RedirectException("/index");
        }
    }

    private void addArticle(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        String title = request.getParameter("title");
        String text = request.getParameter("text");
        Article article = new Article(getUser().getId(), title, text);
        articleService.validateArticle(article);
        articleRepository.save(new Article());
        throw new RedirectException("/index");
    }
}
