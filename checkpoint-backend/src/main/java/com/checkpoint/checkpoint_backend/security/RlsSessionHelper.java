package com.checkpoint.checkpoint_backend.security;

import jakarta.persistence.EntityManager;

public class RlsSessionHelper {

    public static void applyCurrentUser(EntityManager em) {
        UUID_CHECK:
        {
            java.util.UUID userId = TenantContext.getUserId();
            if (userId == null) return;

            em.createNativeQuery("SELECT set_config('app.current_user_id', :uid, true)")
                    .setParameter("uid", userId.toString())
                    .getSingleResult();
        }
    }
}
