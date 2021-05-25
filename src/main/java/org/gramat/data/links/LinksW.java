package org.gramat.data.links;

import org.gramat.graphs.links.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class LinksW implements Links {

    final ArrayList<Link> links;

    LinksW() {
        links = new ArrayList<>();
    }

    LinksW(Collection<Link> links) {
        this.links = new ArrayList<>(links);
    }

    public void add(Link link) {
        links.add(link);
    }

    public void removeAll(Links links) {
        for (var link : links) {
            this.links.remove(link);
        }
    }

    @Override
    public Links copyR() {
        return new LinksR(links.toArray(new Link[0]));
    }

    @Override
    public LinksW copyW() {
        return new LinksW(links);
    }

    @Override
    public Iterator<Link> iterator() {
        return links.iterator();
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public boolean isEmpty() {
        return links.isEmpty();
    }

    @Override
    public boolean isPresent() {
        return !links.isEmpty();
    }

}
