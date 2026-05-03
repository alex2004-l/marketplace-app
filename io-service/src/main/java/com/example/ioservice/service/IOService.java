package com.example.ioservice.service;

import com.example.ioservice.dto.ProductDto;
import com.example.ioservice.dto.UserDto;
import com.example.ioservice.dto.WishlistDTOs.WishlistDTO;
import com.example.ioservice.dto.WishlistDTOs.WishlistAddDTO;
import com.example.ioservice.dto.WishlistItemDTOs.WishlistItemDTO;
import com.example.ioservice.dto.WishlistItemDTOs.WishlistItemAddDTO;
import com.example.ioservice.model.ProductModel;
import com.example.ioservice.model.UserModel;
import com.example.ioservice.model.WishlistItemModel;
import com.example.ioservice.model.WishlistModel;
import com.example.ioservice.repository.ProductRepository;
import com.example.ioservice.repository.UserRepository;
import com.example.ioservice.repository.WishlistItemRepository;
import com.example.ioservice.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IOService {
    private final ProductRepository      productRepository;
    private final UserRepository         userRepository;
    private final WishlistRepository     wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;

    public ProductDto addProduct(ProductDto productDto) {
        ProductModel productModel = new ProductModel(productDto.productId(), productDto.productName(),
                productDto.price(), productDto.description(), productDto.ownerId(), productDto.quantity());
        productRepository.save(productModel);

        return new ProductDto(productModel.getProductId(), productModel.getProductName(), productModel.getPrice(),
                productModel.getDescription(), productModel.getOwnerId(), productModel.getQuantity());
    }

    public List<ProductDto> seeAllProducts() {
        return productRepository.findAll().stream()
                .map(productModel -> new ProductDto(productModel.getProductId(), productModel.getProductName(),
                        productModel.getPrice(), productModel.getDescription(), productModel.getOwnerId(),
                        productModel.getQuantity())).toList();
    }

    public ProductDto getProductById(Long productId) {
        Optional<ProductModel> productModelOptional = productRepository.findById(productId);

        return productModelOptional.map(productModel -> new ProductDto(productModel.getProductId(),
                productModel.getProductName(), productModel.getPrice(), productModel.getDescription(),
                productModel.getOwnerId(), productModel.getQuantity())).orElseThrow(() -> new RuntimeException("No product with id " + productId + "!"));
    }

    public List<ProductDto> searchByName(String name) {
        return productRepository.findByProductNameIgnoreCaseStartingWith(name.toLowerCase()).stream()
                .map(productModel -> new ProductDto(productModel.getProductId(), productModel.getProductName(),
                        productModel.getPrice(), productModel.getDescription(), productModel.getOwnerId(),
                        productModel.getQuantity())).toList();
    }

    public String changeAddress(String username, String address) {
        Optional<UserModel> userModelOptional = Optional.ofNullable(userRepository.findByUsername(username));

        if (userModelOptional.isPresent()) {
            UserModel userModel = userModelOptional.get();
            userModel.setAddress(address);
            userRepository.save(userModel);
            return "Address changed successfully!";
        } else {
            return "User doesn't exist!";
        }
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Transactional
    public WishlistDTO addWishlist(WishlistAddDTO wishlistAddDTO) {
        boolean exists = wishlistRepository.existsByUserIdAndWishlistName(wishlistAddDTO.userId(), wishlistAddDTO.wishlistName());
        if (exists) {
            throw new IllegalStateException("Wishlist already exists!");
        }

        WishlistModel wishlist = new WishlistModel();
        wishlist.setWishlistName(wishlistAddDTO.wishlistName());
        wishlist.setUserId(wishlistAddDTO.userId());

        wishlistRepository.save(wishlist);

        return new WishlistDTO(
                wishlist.getWishlistId(),
                wishlist.getWishlistName(),
                wishlist.getUserId());
    }

    @Transactional
    public WishlistDTO getWishlist(Long wishlistId, Long userId) {
        WishlistModel wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        if (!wishlist.getUserId().equals(userId)) {
            throw new SecurityException("User is not authorized to access wishlist");
        }

        return new WishlistDTO(
          wishlist.getWishlistId(),
          wishlist.getWishlistName(),
          wishlist.getUserId()
        );
    }

    @Transactional
    public void deleteWishlist(Long wishlistId, Long userId) {
        WishlistModel wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        if (!wishlist.getUserId().equals(userId)) {
            throw new SecurityException("User is not authorized to access wishlist");
        }

        wishlistRepository.deleteById(wishlistId);
    }

    @Transactional
    public WishlistItemDTO addWishlistItem(WishlistItemAddDTO wishlistItemAddDTO) {
        WishlistModel wishlist = wishlistRepository.findById(wishlistItemAddDTO.wishlistId())
                .orElseThrow(() -> new IllegalArgumentException("Wishlist doesn't exist!"));

        if (!wishlist.getUserId().equals(wishlistItemAddDTO.userId())) {
            throw new SecurityException("User is not authorized to access wishlist");
        }

        boolean product_exists = productRepository.existsById(wishlistItemAddDTO.productId());
        if (!product_exists) {
            throw new IllegalArgumentException("Product doesn't exist!");
        }

        boolean exists = wishlistItemRepository.existsByWishlistModelAndProductId(wishlist, wishlistItemAddDTO.productId());
        if (exists) {
            throw new IllegalStateException("Wishlist item already exists!");
        }

        WishlistItemModel wishlistItemModel = new WishlistItemModel();
        wishlistItemModel.setProductId(wishlistItemAddDTO.productId());
        wishlistItemModel.setCreatedAt(LocalDateTime.now());

        wishlist.addItem(wishlistItemModel);

        wishlistItemRepository.save(wishlistItemModel);

        return new WishlistItemDTO(
                wishlistItemModel.getWishlistItemId(),
                wishlistItemModel.getWishlistModel().getWishlistId(),
                wishlistItemModel.getProductId(),
                wishlistItemModel.getCreatedAt()
        );
    }

    @Transactional
    public WishlistItemDTO getWishlistItem(Long wishlistItemId, Long userId) {
        WishlistItemModel wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item doesn't exist!"));

        if (!wishlistItem.getWishlistModel().getUserId().equals(userId)) {
            throw new SecurityException("User is not authorized to access wishlist item");
        }

        return new WishlistItemDTO(
                wishlistItem.getWishlistItemId(),
                wishlistItem.getWishlistModel().getWishlistId(),
                wishlistItem.getProductId(),
                wishlistItem.getCreatedAt()
        );
    }

    @Transactional
    public List<WishlistItemDTO> getWishlistItems(String name, Long userId) {
        WishlistModel wishlistModel = wishlistRepository.findByUserIdAndWishlistName(userId, name)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        List<WishlistItemModel> wishlistItems = wishlistItemRepository.findByWishlistModel(wishlistModel);

        return wishlistItems.stream()
                .map(item -> new WishlistItemDTO(
                        item.getWishlistItemId(),
                        item.getWishlistModel().getWishlistId(),
                        item.getProductId(),
                        item.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteWishlistItem(Long wishlistItemId, Long userId) {
        WishlistItemModel wishlistItem = wishlistItemRepository.findByWishlistItemId(wishlistItemId);

        if (wishlistItem == null) {
            throw new IllegalArgumentException("Wishlist item doesn't exist!");
        }

        WishlistModel wishlist = wishlistItem.getWishlistModel();

        if (!wishlist.getUserId().equals(userId)) {
            throw new SecurityException("User not authorized to delete wishlist item");
        }

        wishlist.removeItem(wishlistItem);

        wishlistItemRepository.delete(wishlistItem);
    }
}
