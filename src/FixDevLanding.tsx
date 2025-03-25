import React, {useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {motion} from "framer-motion";

const content = {
    ru: {
        title: "FixDev.io",
        subtitle: "Разработка backend-систем на Kotlin • Java • Spring • Temporal",
        intro: [
            "Привет! Меня зовут Константин Пиксотов. Я backend-инженер и обожаю создавать надёжные, масштабируемые системы.",
            "Мой фокус — архитектура ПО, event-driven подходы и backend-инфраструктура.",
            "Особенно увлекаюсь Temporal, архитектурной чистотой и практиками эффективного Kotlin.",
            "Хочеться поделиться с миром системой Compaing Manager для управления коммуникациями с клиентами"
        ],
        connect: "📫 Connect with me",
        projects: "📂 Featured Projects",
        technologies: "🛠 Tech Stack",
        cta: "🚀 Let's work together",
        footer: "Orchestrating Backends",
        projectList: [
            "Campaign Manager: backend-driven marketing automation with segmentation, contact policy, and analytics.",
        ],
        techList: [
            "Kotlin, Java, Spring Boot",
            "Temporal.io, Event-driven architectures",
            "PostgreSQL, Kafka, Redis",
            "Kubernetes, GitHub Actions, CI/CD"
        ]
    },
    en: {
        title: "FixDev.io",
        subtitle: "Backend systems with Kotlin • Java • Spring • Temporal",
        intro: [
            "Hi! I'm Konstantin Piksotov, a backend engineer passionate about building reliable and scalable systems.",
            "My focus is software architecture, event-driven designs, and backend infrastructure.",
            "I'm especially into Temporal, clean architecture, and practical Kotlin.",
            "I'm excited to share a system called Campaign Manager for managing client communications."
        ],
        connect: "📫 Connect with me",
        projects: "📂 Featured Projects",
        technologies: "🛠 Tech Stack",
        cta: "🚀 Let's work together",
        footer: "Orchestrating Backends",
        projectList: [
            "Campaign Manager: backend-driven marketing automation with segmentation, contact policy, and analytics.",
        ],
        techList: [
            "Kotlin, Java, Spring Boot",
            "Temporal.io, Event-driven architectures",
            "PostgreSQL, Kafka, Redis",
            "Kubernetes, GitHub Actions, CI/CD"
        ]
    }
};

const FixDevLanding = () => {
    const {lang} = useParams();
    const [selectedLang, setSelectedLang] = useState(lang || "en");
    const navigate = useNavigate();

    const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const newLang = e.target.value;
        setSelectedLang(newLang);
        navigate(`/${newLang}`);
    };

    const t = content[selectedLang];

    return (
        <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white font-sans">
            <header
                className="w-full px-6 py-4 border-b border-gray-800 shadow-sm flex justify-between items-center max-w-6xl mx-auto">
                <div className="flex items-center space-x-4">
                    <img src="/img/avatar_circular.png" alt="Константин Пиксотов"
                         className="w-20 h-20 sm:w-32 sm:h-32 rounded-full object-cover border border-gray-600 shadow-sm"/>
                </div>
                <img src="/img/fixdev-logo-horizontal.png" alt="FixDev Logo" className="h-20"/>
                <div>
                    <select
                        value={selectedLang}
                        onChange={handleLanguageChange}
                        className="bg-gray-800 border border-gray-700 text-white py-2 px-3 rounded shadow-sm focus:outline-none"
                    >
                        <option value="ru">🇷🇺 Русский</option>
                        <option value="en">🇬🇧 English</option>
                    </select>
                </div>
            </header>

            <motion.div
                initial={{opacity: 0, y: 40}}
                animate={{opacity: 1, y: 0}}
                transition={{duration: 0.8}}
                className="max-w-3xl mx-auto py-12 px-6"
            >
                <h1 className="text-4xl md:text-6xl font-bold mb-4 text-blue-400">{t.title}</h1>
                <p className="text-lg md:text-xl text-gray-400 mb-6">{t.subtitle}</p>

                <div className="space-y-4 text-gray-200">
                    {t.intro.map((p, idx) => <p key={idx}>{p}</p>)}
                </div>

                <div className="mt-10">
                    <h2 className="text-xl font-semibold mb-3 text-blue-300">{t.connect}</h2>
                    <ul className="space-y-2 text-gray-300">
                        <li><a href="https://t.me/fixdev_io" className="text-blue-400 hover:underline">@fixdev_io
                            Telegram</a></li>
                        <li><a href="https://github.com/fixdev-io" className="text-blue-400 hover:underline">GitHub:
                            fixdev-io</a></li>
                        <li><a href="mailto:fixdev.io@gmail.com" className="text-blue-400 hover:underline">Email:
                            fixdev.io@gmail.com</a></li>
                    </ul>
                </div>

                <div className="mt-14">
                    <h2 className="text-xl font-semibold mb-3 text-blue-300">{t.projects}</h2>
                    <ul className="list-disc list-inside space-y-2 text-gray-300">
                        {t.projectList.map((p, idx) => <li key={idx}>{p}</li>)}
                    </ul>
                </div>

                <div className="mt-14">
                    <h2 className="text-xl font-semibold mb-3 text-blue-300">{t.technologies}</h2>
                    <ul className="list-disc list-inside space-y-1 text-gray-300">
                        {t.techList.map((tech, idx) => <li key={idx}>{tech}</li>)}
                    </ul>
                </div>

                <div className="mt-12 text-center">
                    <a
                        href="mailto:fixdev.io@gmail.com"
                        className="inline-block px-6 py-3 bg-blue-500 text-white font-semibold rounded-xl shadow hover:bg-blue-600 transition"
                    >
                        {t.cta}
                    </a>
                </div>

                <footer className="mt-16 text-sm text-gray-500 text-center">
                    © {new Date().getFullYear()} FixDev.io — {t.footer}
                </footer>
            </motion.div>
        </div>
    );
};

export default FixDevLanding;
