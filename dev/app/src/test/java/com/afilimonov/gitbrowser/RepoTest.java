package com.afilimonov.gitbrowser;

import com.afilimonov.gitbrowser.model.Repo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-15.
 * test for Repo serialization
 */
public class RepoTest {

    @Test
    public void testSerialization() {
        Repo repo = new Repo();
        repo.htmlUrl = "theUrl";
        repo.id = "theId";
        repo.name = "theName";
        repo.fullName = "theFullName";

        String json = repo.toJson();
        Repo newRepo = Repo.fromJson(json);

        assertTrue("repo json serialization",
                repo.id.equals(newRepo.id)
                        && repo.name.equals(newRepo.name)
                        && repo.fullName.equals(newRepo.fullName)
                        && repo.htmlUrl.equals(newRepo.htmlUrl)
        );
    }
}
