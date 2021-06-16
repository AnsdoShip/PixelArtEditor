/*
 * Copyright (C) 2021 AnsdoShip Studio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.ansdoship.pixelarteditor;

import com.ansdoship.pixelarteditor.util.ApplicationUtils;

final class Copyright {

    private Copyright(){}

    public static final String APP_COPYRIGHT = "/*\n" +
            " * Copyright (C) 2021 AnsdoShip Studio\n" +
            " *\n" +
            " * This program is free software: you can redistribute it and/or modify\n" +
            " * it under the terms of the GNU General Public License as published by\n" +
            " * the Free Software Foundation, either version 3 of the License, or\n" +
            " * (at your option) any later version.\n" +
            " *\n" +
            " * This program is distributed in the hope that it will be useful,\n" +
            " * but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
            " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
            " * GNU General Public License for more details.\n" +
            " *\n" +
            " * You should have received a copy of the GNU General Public License\n" +
            " * along with this program.  If not, see <http://www.gnu.org/licenses/>\n" +
            " */";

    public static final String ANDROIDX_APPCOMPAT_COPYRIGHT = "/*\n" +
            " * Copyright (C) 2012 The Android Open Source Project\n" +
            " *\n" +
            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            " * you may not use this file except in compliance with the License.\n" +
            " * You may obtain a copy of the License at\n" +
            " *\n" +
            " *      http://www.apache.org/licenses/LICENSE-2.0\n" +
            " *\n" +
            " * Unless required by applicable law or agreed to in writing, software\n" +
            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            " * See the License for the specific language governing permissions and\n" +
            " * limitations under the License.\n" +
            " */";

    public static final String ANDROIDX_CONSTRAINTLAYOUT_COPYRIGHT = "/*\n" +
            " * Copyright (C) 2016 The Android Open Source Project\n" +
            " *\n" +
            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            " * you may not use this file except in compliance with the License.\n" +
            " * You may obtain a copy of the License at\n" +
            " *\n" +
            " *      http://www.apache.org/licenses/LICENSE-2.0\n" +
            " *\n" +
            " * Unless required by applicable law or agreed to in writing, software\n" +
            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            " * See the License for the specific language governing permissions and\n" +
            " * limitations under the License.\n" +
            " */";

    public static final String ANDROIDX_PREFERENCE_COPYRIGHT = "/*\n" +
            " * Copyright (C) 2018 The Android Open Source Project\n" +
            " *\n" +
            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            " * you may not use this file except in compliance with the License.\n" +
            " * You may obtain a copy of the License at\n" +
            " *\n" +
            " *      http://www.apache.org/licenses/LICENSE-2.0\n" +
            " *\n" +
            " * Unless required by applicable law or agreed to in writing, software\n" +
            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            " * See the License for the specific language governing permissions and\n" +
            " * limitations under the License.\n" +
            " */";

    public static final String ANDROIDX_CARDVIEW_COPYRIGHT = "/*\n" +
            " * Copyright (C) 2018 The Android Open Source Project\n" +
            " *\n" +
            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            " * you may not use this file except in compliance with the License.\n" +
            " * You may obtain a copy of the License at\n" +
            " *\n" +
            " *      http://www.apache.org/licenses/LICENSE-2.0\n" +
            " *\n" +
            " * Unless required by applicable law or agreed to in writing, software\n" +
            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            " * See the License for the specific language governing permissions and\n" +
            " * limitations under the License.\n" +
            " */";

    public static final String APACHE_COMMONS_IO_COPYRIGHT = "/*\n" +
            " * Licensed to the Apache Software Foundation (ASF) under one or more\n" +
            " * contributor license agreements.  See the NOTICE file distributed with\n" +
            " * this work for additional information regarding copyright ownership.\n" +
            " * The ASF licenses this file to You under the Apache License, Version 2.0\n" +
            " * (the \"License\"); you may not use this file except in compliance with\n" +
            " * the License.  You may obtain a copy of the License at\n" +
            " * \n" +
            " *      http://www.apache.org/licenses/LICENSE-2.0\n" +
            " * \n" +
            " * Unless required by applicable law or agreed to in writing, software\n" +
            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            " * See the License for the specific language governing permissions and\n" +
            " * limitations under the License.\n" +
            " */";

    private static final String COPYRIGHT =
            APP_COPYRIGHT +
            "\n\n" +
            "androidx.appcompat:appcompat:1.2.0" +
            "\n" +
            ANDROIDX_APPCOMPAT_COPYRIGHT +
            "\n\n" +
            "androidx.constraintlayout:constraintlayout:2.0.4" +
            "\n" +
            ANDROIDX_CONSTRAINTLAYOUT_COPYRIGHT +
            "\n\n" +
            "androidx.preference:preference:1.1.1" +
            "\n" +
            ANDROIDX_PREFERENCE_COPYRIGHT +
            "\n\n" +
            "androidx.cardview:cardview:1.0.0" +
            "\n" +
            ANDROIDX_CARDVIEW_COPYRIGHT +
            "\n\n" +
            "org.apache.commons.io:commonsIO:2.5.0" +
            "\n" +
            APACHE_COMMONS_IO_COPYRIGHT;

    public static String getCopyright() {
        return ApplicationUtils.getResources().getString(R.string.app_name) + "\n" + COPYRIGHT;
    }

}
