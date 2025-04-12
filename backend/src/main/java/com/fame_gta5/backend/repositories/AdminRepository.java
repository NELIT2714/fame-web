package com.fame_gta5.backend.repositories;

import com.fame_gta5.backend.schemas.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminsRepository extends JpaRepository<Admin, Integer> {
}
