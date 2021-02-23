/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.internal;

import com.google.gson.DYI.DYIStructureSingleton;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class selects which fields and types to omit. It is configurable,
 * supporting version attributes {@link Since} and {@link Until}, modifiers,
 * synthetic fields, anonymous and local classes, inner classes, and fields with
 * the {@link Expose} annotation.
 *
 * <p>This class is a type adapter factory; types that are excluded will be
 * adapted to null. It may delegate to another type adapter if only one
 * direction is excluded.
 *
 * @author Joel Leitch
 * @author Jesse Wilson
 */
public final class Excluder implements TypeAdapterFactory, Cloneable {
  private static final double IGNORE_VERSIONS = -1.0d;
  public static final Excluder DEFAULT = new Excluder();

  private double version = IGNORE_VERSIONS;
  private int modifiers = Modifier.TRANSIENT | Modifier.STATIC;
  private boolean serializeInnerClasses = true;
  private boolean requireExpose;
  private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
  private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();

  @Override protected Excluder clone() {
    try {
      return (Excluder) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  public Excluder withVersion(double ignoreVersionsAfter) {
    Excluder result = clone();
    result.version = ignoreVersionsAfter;
    return result;
  }

  public Excluder withModifiers(int... modifiers) {
    Excluder result = clone();
    result.modifiers = 0;
    for (int modifier : modifiers) {
      result.modifiers |= modifier;
    }
    return result;
  }

  public Excluder disableInnerClassSerialization() {
    Excluder result = clone();
    result.serializeInnerClasses = false;
    return result;
  }

  public Excluder excludeFieldsWithoutExposeAnnotation() {
    Excluder result = clone();
    result.requireExpose = true;
    return result;
  }

  public Excluder withExclusionStrategy(ExclusionStrategy exclusionStrategy,
      boolean serialization, boolean deserialization) {
    Excluder result = clone();
    if (serialization) {
      result.serializationStrategies = new ArrayList<ExclusionStrategy>(serializationStrategies);
      result.serializationStrategies.add(exclusionStrategy);
    }
    if (deserialization) {
      result.deserializationStrategies
          = new ArrayList<ExclusionStrategy>(deserializationStrategies);
      result.deserializationStrategies.add(exclusionStrategy);
    }
    return result;
  }

  public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
    Class<?> rawType = type.getRawType();
    boolean excludeClass = excludeClassChecks(rawType);

    final boolean skipSerialize = excludeClass || excludeClassInStrategy(rawType, true);
    final boolean skipDeserialize = excludeClass ||  excludeClassInStrategy(rawType, false);

    if (!skipSerialize && !skipDeserialize) {
      return null;
    }

    return new TypeAdapter<T>() {
      /** The delegate is lazily created because it may not be needed, and creating it may fail. */
      private TypeAdapter<T> delegate;

      @Override public T read(JsonReader in) throws IOException {
        if (skipDeserialize) {
          in.skipValue();
          return null;
        }
        return delegate().read(in);
      }

      @Override public void write(JsonWriter out, T value) throws IOException {
        if (skipSerialize) {
          out.nullValue();
          return;
        }
        delegate().write(out, value);
      }

      private TypeAdapter<T> delegate() {
        TypeAdapter<T> d = delegate;
        return d != null
            ? d
            : (delegate = gson.getDelegateAdapter(Excluder.this, type));
      }
    };
  }


  /**
   * Sets flags for each branching decision in the function structure, -
   * through the implementation of singleton class DYIStructureSingleton.
   * The coverage tool is hooked to Maven and is automatically with by building a lifestage phase that involves testing.
   * If a branch is reached the flag with the corresponding id is set to true.
   * To handle ternary operations, the chosen implementation creates a if- statement and a corresponding flag for each of conditions.
   * The flags are numbered in a ascending order.
   * Total number of flags is 30.
   * @param field Field object
   * @param serialize boolean value
   * @return boolean
   */
  public boolean excludeField(Field field, boolean serialize) {
    DYIStructureSingleton s = DYIStructureSingleton.getInstance();
    if ((modifiers & field.getModifiers()) != 0) {
      s.flag[0]=true;
      return true;
    } else{
        s.flag[1]=true;
    }


    if (version != Excluder.IGNORE_VERSIONS && !isValidVersion(field.getAnnotation(Since.class), field.getAnnotation(Until.class))) {
      if (version != Excluder.IGNORE_VERSIONS) {
        s.flag[2]=true;
      }
      if (!isValidVersion(field.getAnnotation(Since.class), field.getAnnotation(Until.class))) {
        s.flag[3]=true;
      }

      return true;
    } else{
      if (version == Excluder.IGNORE_VERSIONS) {
        s.flag[4]=true;
      }
      if (isValidVersion(field.getAnnotation(Since.class), field.getAnnotation(Until.class))) {
        s.flag[5]=true;
      }
      }

    if (field.isSynthetic()) {
      s.flag[6]=true;
      return true;
    } else{s.flag[7]=true;}

    if (requireExpose) {
      s.flag[8]=true;
      Expose annotation = field.getAnnotation(Expose.class);
      //8 branches if, divide?
      if (annotation == null || (serialize ? !annotation.serialize() : !annotation.deserialize())) {
        if (annotation == null) {
          s.flag[9]=true;
        }
        if (annotation != null) {
          s.flag[10]=true;
        }
        if (serialize == true) {
          s.flag[11]=true;
        }
        if (serialize == false) {
          s.flag[12]=true;
        }

        return true;
      } else{
        if (annotation != null) {
          s.flag[13]=true;
        }
        if (serialize == true) {
          s.flag[15]=true;
        }
        if (serialize == false) {
          s.flag[16]=true;
        }}


    } else{s.flag[17]=true;}

    if (!serializeInnerClasses && isInnerClass(field.getType())) {
      if (!serializeInnerClasses) {
        s.flag[18]=true;
      }
      if (isInnerClass(field.getType())){
        s.flag[19]=true;
      }

      return true;
    } else{
      if (serializeInnerClasses == true) {
        s.flag[20]=true;
      }
      if (isInnerClass(field.getType()) == false) {
        s.flag[21]=true;
      }
    }


    if (isAnonymousOrLocal(field.getType())) {
      s.flag[22]=true;
      return true;
    } else{s.flag[23]=true;}
    List<ExclusionStrategy> list = serialize ? serializationStrategies : deserializationStrategies;
    if (serialize == true){
      s.flag[24]=true;
    } else {s.flag[25]=true;}

    if (!list.isEmpty()) {
      s.flag[26]=true;
      FieldAttributes fieldAttributes = new FieldAttributes(field);
      for (ExclusionStrategy exclusionStrategy : list) {
        if (exclusionStrategy.shouldSkipField(fieldAttributes)) {
          s.flag[27]=true;
          return true;
        } else{s.flag[28]=true;}
      }
    } else{s.flag[29]=true;}
    s.flag[14]=true;
    return false;
  }

  private boolean excludeClassChecks(Class<?> clazz) {
      if (version != Excluder.IGNORE_VERSIONS && !isValidVersion(clazz.getAnnotation(Since.class), clazz.getAnnotation(Until.class))) {
          return true;
      }

      if (!serializeInnerClasses && isInnerClass(clazz)) {
          return true;
      }

      if (isAnonymousOrLocal(clazz)) {

          return true;
      }

      return false;
  }

  public boolean excludeClass(Class<?> clazz, boolean serialize) {
      return excludeClassChecks(clazz) ||
              excludeClassInStrategy(clazz, serialize);
  }

  private boolean excludeClassInStrategy(Class<?> clazz, boolean serialize) {
      List<ExclusionStrategy> list = serialize ? serializationStrategies : deserializationStrategies;
      for (ExclusionStrategy exclusionStrategy : list) {
          if (exclusionStrategy.shouldSkipClass(clazz)) {
              return true;
          }
      }
      return false;
  }

  private boolean isAnonymousOrLocal(Class<?> clazz) {
    return !Enum.class.isAssignableFrom(clazz)
        && (clazz.isAnonymousClass() || clazz.isLocalClass());
  }

  private boolean isInnerClass(Class<?> clazz) {
    return clazz.isMemberClass() && !isStatic(clazz);
  }

  private boolean isStatic(Class<?> clazz) {
    return (clazz.getModifiers() & Modifier.STATIC) != 0;
  }

  private boolean isValidVersion(Since since, Until until) {
    return isValidSince(since) && isValidUntil(until);
  }

  private boolean isValidSince(Since annotation) {
    if (annotation != null) {
      double annotationVersion = annotation.value();
      if (annotationVersion > version) {
        return false;
      }
    }
    return true;
  }

  private boolean isValidUntil(Until annotation) {
    if (annotation != null) {
      double annotationVersion = annotation.value();
      if (annotationVersion <= version) {
        return false;
      }
    }
    return true;
  }
}
