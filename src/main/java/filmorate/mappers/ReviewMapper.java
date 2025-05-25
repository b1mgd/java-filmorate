package filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import filmorate.dto.NewReviewRequest;
import filmorate.dto.ReviewDto;
import filmorate.dto.UpdateReviewRequest;
import filmorate.model.Review;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {

    public static Review mapToReview(NewReviewRequest request) {
        Review review = new Review();

        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());

        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();

        reviewDto.setReviewId(review.getReviewId());
        reviewDto.setContent(review.getContent());
        reviewDto.setIsPositive(review.getIsPositive());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        reviewDto.setUseful(review.getUseful());

        return reviewDto;
    }

    public static Review updateReviewFields(Review review, UpdateReviewRequest request) {
        if (request.hasContent()) {
            review.setContent(request.getContent());
        }

        if (request.hasRating()) {
            review.setIsPositive(request.getIsPositive());
        }

        return review;
    }
}
