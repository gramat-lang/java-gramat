package org.gramat.condensing.rules;

import org.gramat.condensing.CondensingContext;
import org.gramat.condensing.CondensingRule;
import org.gramat.condensing.helpers.BeginContent;
import org.gramat.expressions.Expression;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.misc.Nop;
import org.gramat.util.ExpressionList;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CondenseAlternation extends CondensingRule<Alternation> {
    @Override
    protected Expression process(Alternation alt, CondensingContext cc) {
        if (alt.items.isEmpty()) {
            return new Nop();
        }
        else if (alt.items.size() == 1) {
            return cc.condense(alt.items.get(0));
        }
        return mergeAndCondense(alt, cc);
    }

    private Expression mergeAndCondense(Alternation alt, CondensingContext cc) {
        var merged = ExpressionList.builder();

        for (var item : alt.items) {
            var newItem = cc.condense(item);

            if (newItem instanceof Alternation) {
                var nested = (Alternation) newItem;

                merged.addAll(nested.items);
            }
            else {
                merged.add(newItem);
            }
        }

        return new Alternation(merged.build());
    }

    private Expression todoMergeBeginAndEnds(Alternation alt, CondensingContext cc) {
        var beginContentList = alt.items
                .map(cc::condense)
                .mapList(BeginContent::of);
        var beginSet = beginContentList.stream()
                .map(bc -> bc.begin)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        var itemsMerged = ExpressionList.builder();

        for (var begin : beginSet) {
            var options = new ArrayList<List<Expression>>();

            for (var beginContent : beginContentList) {
                if (beginContent.begin.equals(begin) && !beginContent.content.isEmpty()) {
                    options.add(beginContent.content);
                }
            }

            var seqItems = ExpressionList.builder();
            seqItems.add(begin);
            if (!options.isEmpty()) {
                var optionsAlt = ExpressionList.builder();

                for (var option : options) {
                    optionsAlt.add(new Sequence(ExpressionList.of(option)));
                }

                seqItems.add(new Alternation(optionsAlt.build()));
            }

            itemsMerged.add(new Sequence(seqItems.build()));
        }

        return new Alternation(itemsMerged.build());
    }
}
