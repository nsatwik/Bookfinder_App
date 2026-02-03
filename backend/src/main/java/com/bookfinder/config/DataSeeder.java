package com.bookfinder.config;

import com.bookfinder.model.Admin;
import com.bookfinder.model.Book;
import com.bookfinder.model.Role;
import com.bookfinder.model.User;
import com.bookfinder.repository.AdminRepository;
import com.bookfinder.repository.BookRepository;
import com.bookfinder.repository.UserRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public DataSeeder(BookRepository bookRepository, UserRepository userRepository, AdminRepository adminRepository) {
    this.bookRepository = bookRepository;
    this.userRepository = userRepository;
    this.adminRepository = adminRepository;
  }

  @Override
  public void run(String... args) {
    if (bookRepository.count() == 0) {
      List<Book> books = List.of(
          new Book(
              "The Atlas of Tomorrow",
              "Rhea Alcott",
              "Speculative fiction about the cost of progress and the maps we leave behind.",
              "Science Fiction",
              2022,
              "https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=600&q=80"
          ),
          new Book(
              "Quiet Orbit",
              "Malik Dorsey",
              "A slow-burn story of a radio operator who hears the last signal from a forgotten mission.",
              "Literary",
              2021,
              "https://images.unsplash.com/photo-1463320726281-696a485928c7?auto=format&fit=crop&w=600&q=80"
          ),
          new Book(
              "Saltwind Harbor",
              "Elena Maris",
              "Coastal mystery that weaves weather logs, family secrets, and a lighthouse keeper.",
              "Mystery",
              2019,
              "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=600&q=80"
          ),
          new Book(
              "Signal and Shadow",
              "Juno Kei",
              "Tech noir thriller about a cryptographer chasing an algorithm that should not exist.",
              "Thriller",
              2023,
              "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=600&q=80"
          )
      );
      bookRepository.saveAll(books);
    }

    if (userRepository.count() == 0) {
      User user = new User();
      user.setUsername("reader");
      user.setEmail("reader@bookfinder.com");
      user.setPasswordHash(encoder.encode("User@123"));
      user.setRole(Role.USER);
      userRepository.save(user);
    }

    if (adminRepository.count() == 0) {
      Admin admin = new Admin();
      admin.setUsername("admin");
      admin.setEmail("admin@bookfinder.com");
      admin.setPasswordHash(encoder.encode("Admin@123"));
      adminRepository.save(admin);
    }
  }
}
