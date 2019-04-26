package run.halo.app.controller.content;

import cn.hutool.core.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import run.halo.app.model.entity.Post;
import run.halo.app.model.entity.Tag;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import run.halo.app.service.ThemeService;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Tag Controller
 *
 * @author ryanwang
 * @date : 2019-03-21
 */
@Controller
@RequestMapping(value = "/tags")
public class ContentTagController {

    private final TagService tagService;

    private final PostTagService postTagService;

    private final OptionService optionService;

    private final ThemeService themeService;

    public ContentTagController(TagService tagService, PostTagService postTagService, OptionService optionService, ThemeService themeService) {
        this.tagService = tagService;
        this.postTagService = postTagService;
        this.optionService = optionService;
        this.themeService = themeService;
    }

    /**
     * All of tags
     *
     * @return template path: themes/{theme}/tags
     */
    @GetMapping
    public String tags() {
        return themeService.render("tags");
    }

    /**
     * List tags by tag slug
     *
     * @param model    model
     * @param slugName slug name
     * @return template path: themes/{theme}/tag
     */
    @GetMapping(value = "{slugName}")
    public String tags(Model model,
                       @PathVariable("slugName") String slugName) {
        return this.tags(model, slugName, 1, Sort.by(DESC, "createTime"));
    }

    /**
     * List tags by tag slug
     *
     * @param model    model
     * @param slugName slug name
     * @param page     current page
     * @return template path: themes/{theme}/tag
     */
    @GetMapping(value = "{slugName}/page/{page}")
    public String tags(Model model,
                       @PathVariable("slugName") String slugName,
                       @PathVariable("page") Integer page,
                       @SortDefault(sort = "createTime", direction = DESC) Sort sort) {
        Tag tag = tagService.getBySlugNameOfNonNull(slugName);

        final Pageable pageable = PageRequest.of(page - 1, optionService.getPostPageSize(), sort);
        Page<Post> posts = postTagService.pagePostsBy(tag.getId(), pageable);
        final int[] rainbow = PageUtil.rainbow(page, posts.getTotalPages(), 3);

        model.addAttribute("is_tags", true);
        model.addAttribute("posts", posts);
        model.addAttribute("rainbow", rainbow);
        model.addAttribute("tag", tag);
        return themeService.render("tag");
    }
}