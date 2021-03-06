package com.buschmais.sarf.core.plugin.api;

import com.buschmais.sarf.core.plugin.api.criterion.RuleBasedCriterionDescriptor;
import com.buschmais.sarf.core.plugin.api.criterion.RuleDescriptor;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ContainedIn {
    Class<? extends RuleBasedCriterionDescriptor<? extends RuleDescriptor>> value();
}
