package org.gramat.data.links;

import org.gramat.graphs.links.Link;
import org.gramat.tools.DataUtils;

import java.util.Iterator;
import java.util.List;

public class LinksR implements Links {

    private final Link[] links;

    LinksR(Link[] links) {
        this.links = links;
    }

    @Override
    public Links copyR() {
        // Since this is immutable, we can safely return the same reference
        return this;
    }

    @Override
    public LinksW copyW() {
        return new LinksW(List.of(links));
    }

    @Override
    public int getCount() {
        return links.length;
    }

    @Override
    public boolean isEmpty() {
        return links.length == 0;
    }

    @Override
    public boolean isPresent() {
        return links.length > 0;
    }

    @Override
    public Iterator<Link> iterator() {
        return DataUtils.iteratorOf(links);
    }
}
