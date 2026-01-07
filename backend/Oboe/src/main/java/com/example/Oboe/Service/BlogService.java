package com.example.Oboe.Service;

import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.DTOs.TopicPostProjection;
import com.example.Oboe.Entity.BaiViet;
import com.example.Oboe.Entity.NguoiDung;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    public BlogService(BlogRepository blogRepository, UserService userService, CommentRepository commentRepository) {
        this.blogRepository = blogRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    //  Lấy danh sách tất cả Blog
    public Map<String, Object> getAllBlogDTOs(int page, int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
            Page<BaiViet> blogPage = blogRepository.findAll(pageable);

            List<BlogDTO> blogDTOs = blogPage.getContent()
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());

            response.put("blogs", blogDTOs);
            response.put("currentPage", blogPage.getNumber());
            response.put("totalPages", blogPage.getTotalPages());
            response.put("totalElements", blogPage.getTotalElements());
            response.put("pageSize", blogPage.getSize());
            response.put("last", blogPage.isLast());
            response.put("code", 200);
            response.put("message", "Lấy danh sách blog thành công.");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "Đã xảy ra lỗi khi lấy danh sách blog.");
            response.put("blogs", Collections.emptyList());
        }
        return response;
    }


    //  Lấy Blog theo ID
    public BlogDTO getBlogDTOById(UUID id) {
        Optional<BaiViet> blogOpt = blogRepository.findById(id);
        if (blogOpt.isEmpty()) {

            return null;
        }
        return toDTO(blogOpt.get());
    }

    // Tạo Blog mới từ DTO và userId
    public BlogDTO createBlogFromDTO(BlogDTO blogDTO, UUID userId) {
        Optional<NguoiDung> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return null;
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime vietnamTime = LocalDateTime.now(vietnamZone);
        BaiViet baiViet = new BaiViet();
        baiViet.setTieuDe(blogDTO.getTitle());
        baiViet.setNoiDung(blogDTO.getContent());
        baiViet.setNguoiDung(userOpt.get());
        baiViet.setNgayTao(vietnamTime);
        baiViet.setNgayCapNhat(vietnamTime);
        //  Thêm tags và topics
        baiViet.setThe(blogDTO.getTags());
        baiViet.setChuDe(blogDTO.getTopics());


        BaiViet saved = blogRepository.save(baiViet);
        return toDTO(saved);
    }

    // Cập nhật blog (chỉ nếu người dùng là chủ sở hữu)
    public BlogDTO updateBlogFromDTO(UUID id, BlogDTO blogDTO, UUID userId) {
        Optional<BaiViet> blogOpt = blogRepository.findById(id);
        if (blogOpt.isEmpty()) return null;

        Optional<NguoiDung> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return null;


        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime vietnamTime = LocalDateTime.now(vietnamZone);
        BaiViet baiViet = blogOpt.get();
        NguoiDung currentNguoiDung = userOpt.get();

        if (baiViet.getNguoiDung() == null || !baiViet.getNguoiDung().getMaNguoiDung().equals(currentNguoiDung.getMaNguoiDung())) {
            return null;
        }
        baiViet.setTieuDe(blogDTO.getTitle());
        baiViet.setNoiDung(blogDTO.getContent());
        baiViet.setNgayCapNhat(vietnamTime);

        // Cập nhật tags và topics
        baiViet.setThe(blogDTO.getTags());
        baiViet.setChuDe(blogDTO.getTopics());

        BaiViet updated = blogRepository.save(baiViet);
        return toDTO(updated);
    }

    //  Xóa blog nếu người dùng là chủ sở hữu
    public boolean deleteBlogById(UUID id, UUID userId) {
        Optional<BaiViet> blogOpt = blogRepository.findById(id);
        if (blogOpt.isEmpty()) return false;

        Optional<NguoiDung> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return false;

        BaiViet baiViet = blogOpt.get();
        NguoiDung nguoiDung = userOpt.get();

        // Kiểm tra quyền sở hữu
        if (baiViet.getNguoiDung() == null || !baiViet.getNguoiDung().getMaNguoiDung().equals(nguoiDung.getMaNguoiDung())) {
            return false;
        }

        blogRepository.deleteById(id);
        return true;
    }

    //  Tìm kiếm Blog theo từ khóa tiêu đề

    public List<BlogDTO> searchBlogs(String keyword, String field) {
        List<BaiViet> baiViets;

        switch (field.toLowerCase()) {
            case "tieuDe":
                baiViets = blogRepository.findByTieuDeContainingIgnoreCase(keyword);
                break;
            case "tags":
                baiViets = blogRepository.findByTheContainingIgnoreCase(keyword);
                break;
            case "topics":
                baiViets = blogRepository.findByChuDeContainingIgnoreCase(keyword);
                break;
            default: // "all"
                baiViets = blogRepository.searchByKeyword(keyword);
                break;
        }

        return baiViets.stream().map(this::toDTO).collect(Collectors.toList());
    }


    //  Lấy tất cả Blog của một User cụ thể phân trang
    public Page<BlogDTO> getAllBlogByUserIds(UUID userId, int page, int size) {
        Optional<NguoiDung> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return Page.empty();
        Pageable pageable = PageRequest.of(page, size);
        Page<BaiViet> blogPage = blogRepository.findBlogsByUserIds(userId, pageable);
        // Chuyển đổi Page<Blog> thành Page<BlogDTO>
        List<BlogDTO> blogDTOs = blogPage.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(blogDTOs, pageable, blogPage.getTotalElements());
    }
    public List<BlogDTO> getAllBlogByUserId(UUID userId) {
        Optional<NguoiDung> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return List.of();

        List<BaiViet> baiViets = blogRepository.findBlogsByUserId(userId);
        return baiViets.stream()
                .map(this::toDTO)
                .toList();
    }

    // lấy 5 chủ đề nổi bật nhất
    public List<TopicPostProjection> getTop5TopicsWithMostPosts() {
        return blogRepository.findTop3BlogsByCommentCount();
    }



    //  Chuyển đổi từ Entity sang DTO
    private BlogDTO toDTO(BaiViet baiViet) {
        BlogDTO dto = new BlogDTO();
        dto.setId(baiViet.getMaBaiViet());


        dto.setTitle(baiViet.getTieuDe());
        dto.setContent(baiViet.getNoiDung());
        dto.setCreatedAt(baiViet.getNgayTao());
        dto.setUpdatedAt(baiViet.getNgayCapNhat());

        dto.setTags(baiViet.getThe());
        dto.setTopics(baiViet.getChuDe());

        if (baiViet.getNguoiDung() != null) {
            dto.setUserId(baiViet.getNguoiDung().getMaNguoiDung());
            dto.setAuthor(baiViet.getNguoiDung().getEmail());
            dto.setAvatarUrl(baiViet.getNguoiDung().getAnhDaiDien());
        }

        // Đếm số comment
        long count = commentRepository.countByMaThamChieu(baiViet.getMaBaiViet());
        dto.setCommentCount((int) count);

        //  Lấy comment gần nhất
        commentRepository.findTopByMaThamChieuOrderByNgayTaoDesc(baiViet.getMaBaiViet())
                .ifPresent(latestComment -> {
                    dto.setLatestCommentTime(latestComment.getNgayTao());
                    dto.setLatestCommenterName(latestComment.getNguoiDung().getEmail());

                });

        return dto;
    }
    public boolean blogExists(UUID id) {
        return blogRepository.existsById(id);
    }

    public boolean deleteBlogAsAdmin(UUID blogId) {
        Optional<BaiViet> blogOpt = blogRepository.findById(blogId);
        if (blogOpt.isEmpty()) return false;

        BaiViet baiViet = blogOpt.get();
        blogRepository.delete(baiViet);
        return true;
    }


}
