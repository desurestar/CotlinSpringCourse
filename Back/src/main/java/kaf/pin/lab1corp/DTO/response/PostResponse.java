package kaf.pin.lab1corp.DTO.response;

public class PostResponse {
    private Long id;
    private String postName;

    public PostResponse() {}

    public PostResponse(Long id, String postName) {
        this.id = id;
        this.postName = postName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }
}
